const express = require("express");
const path = require("path");
const axios = require("axios");

const app = express();
const PORT = process.env.PORT || 3000;
app.use(express.static(__dirname));

console.log(`BFF API URL ${process.env.BFF_API_URL}`);


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
        console.error(`Could not proxy to BFF API: ${error.message}`);
        res.status(500).send("Could not connect to BFF API proxy");
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
    console.log(`Server v${process.env.npm_package_version} is running on port ${PORT}`);
});
