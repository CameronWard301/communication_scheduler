import {useContext} from "react";
import {SnackbarContext} from "../context/SnackbarContext.tsx";
import {AxiosError} from "axios";

export const useErrorHandling = () => {
  const {addSnackbar} = useContext(SnackbarContext);

  const handleError = (error: unknown) => {
    console.error(error);
    if (error instanceof AxiosError) {
      switch (error.code) {
        case "ERR_NETWORK":
          if (error.config && error.config.url) {
            addSnackbar("Network Error. Could not connect to server for url: " + error.config.url + ". Make sure the server is running and accessible", "error");
            return;
          }
      }
      addSnackbar("No response from server", "error");
      return
    }
    addSnackbar("An unknown error occurred", "error");
  }

  return {handleError};

}
