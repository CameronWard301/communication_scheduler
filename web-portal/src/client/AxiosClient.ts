import axios from "axios";
import {ConfigContext} from "../context/ConfigContext.tsx";
import {useContext} from "react";

export function useAxiosClient() {
  const [config] = useContext(ConfigContext);

  if (config.verifyHttps) {
    console.log("SSL Verification is on");
    return axios.create({
      headers: {
        "Content-Type": "application/json",
      }
    });
  } else {
    console.warn("SSL Verification is off");
    return axios.create({
      //httpsAgent: new https.Agent({rejectUnauthorized: false}),
      headers: {
        "Content-Type": "application/json",
      }
    });
  }
}


export default useAxiosClient;
