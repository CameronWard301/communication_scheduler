import axios, {AxiosInstance} from "axios";
import * as https from "https";


let axiosClient:AxiosInstance;

if (process.env.SSL_VERIFICATION) {
  axiosClient = axios.create({
    headers: {
    "Content-Type": "application/json",
    }});
} else {
  axiosClient = axios.create({
    httpsAgent: new https.Agent({rejectUnauthorized: false}),
    headers: {
      "Content-Type": "application/json",
    }
  });
}

export default axiosClient;
