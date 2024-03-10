import {AuthToken} from "../models/AuthToken.ts";
import {ConfigContext} from "../context/ConfigContext.tsx";
import {useContext} from "react";
import Cookies from "js-cookie";
import {useAxiosClientContext} from "../context/AxiosContext.tsx";


export function useToken(): Promise<AuthToken> {
  const [config] = useContext(ConfigContext);
  const client = useAxiosClientContext();
  const token = Cookies.get(`${APP_VERSION}-${config.environment}-communication-scheduler-token`);

  const authenticate = async (): Promise<AuthToken> => {

    return await client
      .post(config.bffBaseUrl + "/auth",
        JSON.stringify(["GATEWAY:WRITE", "GATEWAY:READ", "GATEWAY:DELETE", "PREFERENCES:READ", "PREFERENCES:WRITE", "SCHEDULE:READ", "SCHEDULE:WRITE", "SCHEDULE:DELETE", "WORKFLOW:TERMINATE", "HISTORY:READ"]))
      .then((response) => {
        const token = response.data as AuthToken;
        Cookies.set(`${APP_VERSION}-${config.environment}-communication-scheduler-token`, JSON.stringify(token), {expires: new Date(token.expires)});
        return token;
      })
      .catch((reason) => {
        throw reason;
      });
  }

  if (!token) {
    return authenticate();
  }
  const tokenObject = JSON.parse(token) as AuthToken;
  if (Date.parse(tokenObject.expires) < Date.now()) {
    return authenticate();
  }

  return Promise.resolve(tokenObject);
}


