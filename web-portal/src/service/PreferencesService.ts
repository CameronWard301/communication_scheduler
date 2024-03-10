import {ConfigContext} from "../context/ConfigContext.tsx";
import {useContext} from "react";
import {useToken} from "./AuthenticationService.ts";
import {useStore} from "../context/StoreContext.tsx";
import {Preferences} from "../models/Preferences.ts";
import {useAxiosClientContext} from "../context/AxiosContext.tsx";
import {SnackbarContext} from "../context/SnackbarContext.tsx";
import {useErrorHandling} from "../helper/UseErrorHandling.ts";

export const usePreferencesService = () => {
  const client = useAxiosClientContext();
  const authToken = useToken();
  const rootStore = useStore();
  const [config] = useContext(ConfigContext);
  const {addSnackbar} = useContext(SnackbarContext);
  const {handleError} = useErrorHandling();

  const getPreferences = () => {
    rootStore.preferencesStore.setLoading(true);
    authToken.then((token) => {
      client.get(config.bffBaseUrl + "/preferences", {
        headers: {
          "Authorization": "Bearer " + token.token,
        }
      }).then(response => {
        rootStore.preferencesStore.setFromServer(response.data as Preferences);
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => rootStore.preferencesStore.setLoading(false));
    }).catch(error => {
      handleError(error)
      rootStore.preferencesStore.setLoading(false)
    })
  }

  const setPreferences = () => {
    rootStore.preferencesStore.setLoading(true);
    authToken.then((token) => {
      client.put(config.bffBaseUrl + "/preferences", rootStore.preferencesStore.getPreferencesUpdate(), {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then(response => {
        rootStore.preferencesStore.setFromServer(response.data as Preferences);
        rootStore.preferencesStore.setModalOpen(false)
        addSnackbar("Preferences updated", "success");
      }).catch(error => handleError(error))
        .finally(() => rootStore.preferencesStore.setLoading(false));
    }).catch(error => {
      handleError(error)
      rootStore.preferencesStore.setLoading(false)
    });


  }
  return {getPreferences, setPreferences}
}
