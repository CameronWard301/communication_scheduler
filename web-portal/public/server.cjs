const express = require("express");
const path = require("path");
const axios = require("axios");
const fs = require("fs");

const app = express();
const PORT = process.env.PORT || 3000;
app.use(express.static(__dirname));

console.log(`BFF API URL ${process.env.BFF_API_URL}`);

let version;
try {
    version = fs.readFileSync('version', 'utf8');
} catch (err) {
    console.error('Error reading version from file:', err);
}

app.all("/v1/bff/*", async (req, res) => {
    let url = process.env.BFF_API_URL + req.url;
    console.log(`Proxying ${req.method} request to: ${url}`);

    try {
        const {data} = await axios({
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

app.listen(PORT, () => {
    console.log(`Server v${version.trim()} is running on port ${PORT}`);
});
