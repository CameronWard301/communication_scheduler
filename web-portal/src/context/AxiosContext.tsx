//Adapted from: https://codingislove.com/setup-mobx-react-context/
import React from "react";
import axios, {AxiosInstance} from "axios";

const AxiosContext = React.createContext(axios.create({}));

interface AxiosProviderProps {
  children: React.ReactNode;
  client: AxiosInstance;
}

export const AxiosContextProvider = ({children, client}: AxiosProviderProps) => {
  return <AxiosContext.Provider value={client}>{children}</AxiosContext.Provider>;
}

export const useAxiosClientContext = () => {
  return React.useContext(AxiosContext);
}


