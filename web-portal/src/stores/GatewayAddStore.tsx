import {RootStore} from "./RootStore.tsx";
import {Gateway} from "../models/Gateways.ts";
import {action, makeAutoObservable} from "mobx";

export class GatewayAddStore {
  rootStore: RootStore;
  gateway: Gateway = {
    id: "0",
    friendlyName: "",
    description: "",
    endpointUrl: "",
    dateCreated: ""
  };
  isLoading = false;
  confirmModalOpen = false;

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this);
  }

  setGatewayName(name: string) {
    action(() => {
      this.gateway.friendlyName = name;
    })();
  }

  setGatewayDescription(description: string) {
    action(() => {
      this.gateway.description = description;
    })();
  }

  setGatewayEndpointUrl(url: string) {
    action(() => {
      this.gateway.endpointUrl = url;
    })();
  }

  resetGateway() {
    action(() => {
      this.gateway = {
        id: "0",
        friendlyName: "",
        description: "",
        endpointUrl: "",
        dateCreated: ""
      };
    })();
  }

  isFriendlyNameValid() {
    return this.gateway.friendlyName !== "";
  }

  isEndpointUrlValid() {
    return this.gateway.endpointUrl !== "";
  }

  fieldsAreValid() {
    return this.isFriendlyNameValid() && this.isEndpointUrlValid();
  }

  setConfirmModalOpen = (open: boolean) => {
    action(() => {
      this.confirmModalOpen = open;
    })();
  }

  setLoading(loading: boolean) {
    action(() => {
      this.isLoading = loading;
    })()
  }

}
