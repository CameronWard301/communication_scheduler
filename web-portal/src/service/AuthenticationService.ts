import {AuthToken} from "../models/AuthToken.ts";
import {ConfigContext} from "../context/ConfigContext.tsx";
import {useContext} from "react";
import Cookies from "js-cookie";
import {useAxiosClientContext} from "../context/AxiosContext.tsx";


export function useToken(): Promise<AuthToken> {
  const token = Cookies.get(APP_VERSION + "-communication-scheduler-token");
  if (!token) {
    return useAuthenticate();
  }
  const tokenObject: AuthToken = JSON.parse(token);
  if (Date.parse(tokenObject.expires) < Date.now()) {
    return useAuthenticate();
  }

  return Promise.resolve(tokenObject);
}

async function useAuthenticate(): Promise<AuthToken> {
  const [config] = useContext(ConfigContext);
  const client = useAxiosClientContext();
  return await client
    .post(config.bffBaseUrl + "/auth",
      JSON.stringify(["GATEWAY:WRITE", "GATEWAY:READ", "PREFERENCES:READ", "PREFERENCES:WRITE", "SCHEDULE:READ", "SCHEDULE:WRITE", "SCHEDULE:DELETE", "WORKFLOW:TERMINATE", "HISTORY:READ"]))
    .then((response) => {
      const token: AuthToken = response.data;
      Cookies.set(APP_VERSION + "-communication-scheduler-token", JSON.stringify(token), {expires: new Date(token.expires)});
      return token;
    })
    .catch((reason) => {
      console.log(reason);
      throw reason;
    });
}
