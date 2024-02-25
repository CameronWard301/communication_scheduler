import {AlertColor} from "@mui/material";
import React, {useState} from "react";
import SnackbarComponent, {SnackbarMessage} from "../components/snackbar";

export type SnackbarContextType = {
  addSnackbar: (message: string, severity: AlertColor) => void;
};

type CampaignQueryProviderProps = {
  children: React.ReactNode;
};

export const SnackbarContext = React.createContext<SnackbarContextType>({
  addSnackbar: () => {
  },
});

export const SnackbarContextProvider = ({children}: CampaignQueryProviderProps) => {
  const [snackPack, setSnackPack] = useState<SnackbarMessage[]>([]);

  const addSnackbar = (message: string, severity: AlertColor) => {
    setSnackPack((prev) => [...prev, {message, severity, key: new Date().getTime()}]);
  };

  return (
    <SnackbarContext.Provider value={{addSnackbar}}>
      {children}
      <SnackbarComponent snackPack={snackPack} setSnackPack={setSnackPack}/>
    </SnackbarContext.Provider>
  );
};
