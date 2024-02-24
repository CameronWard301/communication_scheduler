import express, { type Express } from "express";
import dotenv from "dotenv";

import swaggerUi from "swagger-ui-express";
import authenticationController from "./authentication/controllers/authentication-controller";
import swaggerDocument from "./swagger_output.json";

dotenv.config();

const app: Express = express();
const port = process.env.PORT || 3000;
const router = require("express").Router();

router.use("/api-docs", swaggerUi.serve);
router.get("/api-docs", swaggerUi.setup(swaggerDocument));
router.use(express.json());
app.use(router);
app.use("", authenticationController);

app.listen(port, () => {
  console.log(`[server]: Server is running at http://localhost:${port}`);
  console.log("[server]: SSL Verification: " + process.env.SSL_VERIFICATION);
  console.log("[server]: Auth service at: " + process.env.AUTH_API_URL);
});
