import React, {useEffect} from "react";
import {Config} from "../config/Config.ts";
import axios from "axios";
import {CircularProgress} from "@mui/material";

import configJson from "../config/config.json";

const ConfigContext = React.createContext<[Config, React.Dispatch<React.SetStateAction<Config>>]>([{} as Config, () => {
}]);

interface ConfigProviderProps {
  children: React.ReactNode;
}

const ConfigProvider = ({children}: ConfigProviderProps) => {
  const [config, setConfig] = React.useState<Config>(configJson);
  const [loading, setLoading] = React.useState(true);

  useEffect(() => {
    if (import.meta.env.PROD) {
      axios
        .get("/configuration")
        .then((result) => {
          setConfig(result.data as Config);
          setLoading(false);
        })
        .catch((error) => {
          console.log(error);
          setLoading(false);
        });
    } else {
      setConfig(configJson);
      setLoading(false);
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (loading) {
    return <CircularProgress></CircularProgress>; //don't render the rest of the app until the configuration has been loaded
  }

  return <ConfigContext.Provider value={[config, setConfig]}>{children}</ConfigContext.Provider>;
};

export {ConfigContext, ConfigProvider};
