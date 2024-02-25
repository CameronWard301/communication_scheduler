import {ConfigContext} from "../context/ConfigContext.tsx";
import {useContext} from "react";
import {useToken} from "./AuthenticationService.ts";
import {useStore} from "../context/StoreContext.tsx";
import {Preferences} from "../models/Preferences.ts";
import {useAxiosClientContext} from "../context/AxiosContext.tsx";

export const usePreferencesService = ()  => {
  const client = useAxiosClientContext();
  const authToken = useToken();
  const rootStore = useStore();
  const [config] = useContext(ConfigContext);
  const getPreferences = () => {
    authToken.then((token) => {
      client.get(config.bffBaseUrl + "/preferences", {
        headers: {
          "Authorization": "Bearer " + token.token,
        }
      }).then(response => {
        rootStore.preferencesStore.setFromServer(response.data as Preferences);
      })
        .catch(error => {
          throw error;
        })
    }).catch(error => {
      console.error(error);
    })

  }
  return { getPreferences }
}
