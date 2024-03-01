//Adapted from: https://codingislove.com/setup-mobx-react-context/
import React from "react";
import {RootStore} from "../stores/RootStore.tsx";

const StoreContext = React.createContext(new RootStore());

interface StoreProviderProps {
  children: React.ReactNode;
  store: RootStore;
}

export const StoreProvider = ({children, store}: StoreProviderProps) => {
  return <StoreContext.Provider value={store}>{children}</StoreContext.Provider>;
}

export const useStore = () => {
  return React.useContext(StoreContext);
}

export const withStore = (Component: React.ComponentType<any>) => {
  return (props: any) => {
    return <Component {...props} store={useStore()}/>;
  };
}
