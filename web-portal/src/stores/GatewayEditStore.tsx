import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {Gateway} from "../models/Gateways.ts";

export class GatewayEditStore {
  rootStore: RootStore;
  deleteModalOpen = false;
  confirmModalOpen = false;
  isLoading = false;
  storedGateway: Gateway | null = null;
  updatedGateway: Gateway | null = null;
  affectedSchedules = 0

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this);
  }

  setIsLoading(isLoading: boolean) {
    action(() => {
      this.isLoading = isLoading;
    })();
  }

  setGateway(gateway: Gateway) {
    action(() => {
      this.storedGateway = gateway;
    })();
  }

  setUpdatedGateway(gateway: Gateway) {
    action(() => {
      this.updatedGateway = gateway;
    })();
  }

  setGatewayName(name: string) {
    action(() => {
      this.updatedGateway!.friendlyName = name;
    })();
  }

  setGatewayDescription(description: string) {
    action(() => {
      this.updatedGateway!.description = description;
    })();
  }

  setGatewayEndpointUrl(url: string) {
    action(() => {
      this.updatedGateway!.endpointUrl = url;
    })();
  }

  setDeleteModalOpen = (open: boolean) => {
    action(() => {
      this.deleteModalOpen = open;
    })();
  }

  setConfirmModalOpen = (open: boolean) => {
    action(() => {
      this.confirmModalOpen = open;
    })();
  }

  isFriendlyNameValid() {
    return this.updatedGateway!.friendlyName.length > 0;
  }

  isEndpointUrlValid() {
    return this.updatedGateway!.endpointUrl.length > 0;
  }

  hasFriendlyNameChanged() {
    return this.storedGateway!.friendlyName !== this.updatedGateway!.friendlyName;
  }

  hasDescriptionChanged() {
    return this.storedGateway!.description !== this.updatedGateway!.description;
  }

  hasEndpointUrlChanged() {
    return this.storedGateway!.endpointUrl !== this.updatedGateway!.endpointUrl;
  }

  hasGatewayChanged() {
    return this.hasFriendlyNameChanged() || this.hasDescriptionChanged() || this.hasEndpointUrlChanged();
  }

  setAffectedSchedules = (schedules: number) => {
    action(() => {
      this.affectedSchedules = schedules;
    })();
  }

  fieldsAreValid() {
    return this.isFriendlyNameValid() && this.isEndpointUrlValid();
  }
}
