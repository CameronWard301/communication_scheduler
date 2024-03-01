const express = require("express");
const path = require("path");
const axios = require("axios");
const fs = require("fs");
const {createServer, Agent} = require("https");

const app = express();
const PORT = process.env.PORT || 3000;
app.use(express.static(__dirname));

console.log(`BFF API URL ${process.env.BFF_API_URL}`);

let version;
let options = {};
try {
    version = fs.readFileSync('version', 'utf8');
    options = {
        key: process.env.PRIVATE_KEY,
        cert: process.env.CERTIFICATE
    };
} catch (err) {
    console.error('Error reading version from file:', err);
}

let axiosClient;

if (process.env.SSL_VERIFICATION === "true") {
    console.log("SSL Verification is on");
    axiosClient = axios.create({
        headers: {
            "Content-Type": "application/json",
        }
    });
} else {
    console.warn("SSL Verification is off");
    axiosClient = axios.create({
        httpsAgent: new Agent({rejectUnauthorized: false}),
        headers: {
            "Content-Type": "application/json",
        }
    });
}


app.all("/v1/bff/*", async (req, res) => {
    let url = process.env.BFF_API_URL + req.url;
    console.log(`Proxying ${req.method} request to: ${url}`);

    try {
        const {data} = await axiosClient({
            url: url,
            method: req.method,
            responseType: "stream",
            data: req,
            headers: {
                ...req.headers,
            },
        });
        data.pipe(res);
    } catch (error) {
        if (error.response){
            console.log(`Passing error from server as response: ${error.message}`);
            res.status(error.response.status)
            error.response.data.pipe(res);
        } else {
            console.error(`Could not proxy to BFF API: ${error.message}`);
            res.status(500).send("Could not connect to BFF API proxy");
        }
    }
});

app.get("/configuration", (req, res) => {
    console.log("Sending configuration");
    res.sendFile(path.join(__dirname, "config.json"));
})

app.get("/*", (req, res) => {
    console.log("Sending index.html");
    res.sendFile(path.join(__dirname, "index.html"));
})

let server;
try {
    server = createServer(options, app);
} catch (err) {
    console.error('Error creating HTTPS server:', err);
}
server.listen(PORT, () => {
    console.log(`Server v${version.trim()} is running on port ${PORT}`);
});
