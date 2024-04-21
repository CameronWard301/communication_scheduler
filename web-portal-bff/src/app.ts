import express, { type Express } from "express";
import dotenv from "dotenv";

import swaggerUi from "swagger-ui-express";
import authenticationController from "./api/authentication/controllers/authentication-controller";
import swaggerDocument from "./swagger_output.json";
import preferencesController from "./api/preferences/controller/preferences-controller";
import * as fs from "fs";
import * as https from "https";
import gatewayController from "./api/gateways/controller/gateway-controller";
import scheduleController from "./api/schedule/controller/schedule-controller";
import historyController from "./api/history/controllers/history-controller";
import statsController from "./api/stats/controller/stats-controller";
import * as http from "node:http";
import proxy from "./proxy";
import RateLimit from "express-rate-limit";

dotenv.config();

const app: Express = express();
const port = process.env.PORT || 3000;
const router = require("express").Router();
const limiter = RateLimit({
  windowMs: Number(process.env.LIMIT_PERIOD_SECONDS) * 1000, // how long to remember the requests in seconds
  limit: Number(process.env.LIMIT_NUMBER_REQUESTS) // how many requests per windowMs
})


let version: string;
let options = {};
try {
  version = fs.readFileSync("version", "utf8");
  let key = process.env.PRIVATE_KEY;
  let cert = process.env.CERTIFICATE;
  options = {
    key: key,
    cert: cert
  };
} catch (err) {
  console.error("Error reading version from file:", err);
}

const handleProxy = (server: https.Server | http.Server) => {
  server.on('upgrade', (req, socket, head) => {
    let url = (process.env.GRAFANA_API_URL as string).replace("http", "ws");
    console.debug(`Upgrading WebSocket request to stats service: ${url}`);
    proxy.on("error", (err, req, res) => {
      res.destroy()
      console.debug(`Error while proxying request to stats service: ${err.stack}`)
    })
    proxy.ws(req, socket, head, { target: url });

  })
}

router.use("/api-docs", swaggerUi.serve);
router.get("/api-docs", swaggerUi.setup(swaggerDocument));
router.use(express.json());
app.use(limiter)
app.use(router);
app.use(function(_req, res, next) {
  res.header("Access-Control-Allow-Origin", process.env.ALLOW_ORIGIN);
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
  res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");

  next();
});
app.use(authenticationController);
app.use(preferencesController);
app.use(gatewayController);
app.use(scheduleController);
app.use(historyController);
app.use(statsController);

const printConfig = () => {
  console.log("[server]: Limit Window: " + process.env.LIMIT_PERIOD_SECONDS);
  console.log("[server]: Limit Requests: " + process.env.LIMIT_NUMBER_REQUESTS);
  console.log("[server]: SSL Verification: " + process.env.SSL_VERIFICATION);
  console.log("[server]: Auth service at: " + process.env.AUTH_API_URL);
  console.log("[server]: Preferences service at: " + process.env.PREFERENCES_API_URL);
  console.log("[server]: Gateway service at: " + process.env.GATEWAY_API_URL);
  console.log("[server]: Schedule service at: " + process.env.SCHEDULE_API_URL);
  console.log("[server]: History service at: " + process.env.HISTORY_API_URL);
  console.log("[server]: Stats service at: " + process.env.GRAFANA_API_URL);
};

if (process.env.HTTPS_ENABLED == "false") {
  let server = http.createServer(app);

  server.listen(port, () => {
    console.log(`[server]: Server v${version.trim()} is running at http://localhost:${port}`);
    printConfig();
  });

  handleProxy(server)

} else {
  let server = https.createServer(options, app);
  server.listen(port, () => {
    console.log(`[server]: Server v${version.trim()} is running at https://localhost:${port}`);
    printConfig();
  });
  handleProxy(server)
}




