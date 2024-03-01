import express, {type Express} from "express";
import dotenv from "dotenv";

import swaggerUi from "swagger-ui-express";
import authenticationController from "./authentication/controllers/authentication-controller";
import swaggerDocument from "./swagger_output.json";
import preferencesController from "./api/preferences/controller/preferences-controller";
import * as fs from "fs";
import * as https from "https";

dotenv.config();

const app: Express = express();
const port = process.env.PORT || 3000;
const router = require("express").Router();

let version: string;
let options = {};
try {
  version = fs.readFileSync('version', 'utf8');
  console.log('private key: \n' + process.env.PRIVATE_KEY);
  console.log('certificate: \n' + process.env.CERTIFICATE);
  let key = process.env.PRIVATE_KEY;
  let cert = process.env.CERTIFICATE;
  options = {
    key: key,
    cert: cert
  };
} catch (err) {
  console.error('Error reading version from file:', err);
}

router.use("/api-docs", swaggerUi.serve);
router.get("/api-docs", swaggerUi.setup(swaggerDocument));
router.use(express.json());
app.use(router);
app.use(function (_req, res, next) {
  res.header("Access-Control-Allow-Origin", process.env.ALLOW_ORIGIN);
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
  res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

  next();
})
app.use(authenticationController);
app.use(preferencesController);

let server = https.createServer(options, app);

server.listen(port, () => {
  console.log(`[server]: Server v${version.trim()} is running at https://localhost:${port}`);
  console.log("[server]: SSL Verification: " + process.env.SSL_VERIFICATION);
  console.log("[server]: Auth service at: " + process.env.AUTH_API_URL);
  console.log("[server]: Preferences service at: " + process.env.PREFERENCES_API_URL);

});
