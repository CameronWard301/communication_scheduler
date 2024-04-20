import axios, { AxiosInstance } from "axios";
import * as https from "https";
import dotenv from "dotenv";
dotenv.config();

let axiosClient: AxiosInstance;

if (process.env.SSL_VERIFICATION == "false") {
  console.warn("SSL Verification is off");
  axiosClient = axios.create({
    httpsAgent: new https.Agent({ rejectUnauthorized: false }),
    headers: {
      "Content-Type": "application/json"
    }
  });
} else {
  console.log("SSL Verification is on");
  axiosClient = axios.create({
    headers: {
      "Content-Type": "application/json"
    }
  });
}

export default axiosClient;
