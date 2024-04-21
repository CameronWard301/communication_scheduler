import {RootStore} from "./RootStore.tsx";
import {action, makeObservable, observable} from "mobx";
import {Gateway} from "../models/Gateways.ts";
import {GatewayPageStore} from "./GatewayPageStore.tsx";

export class GatewayTableStore extends GatewayPageStore {

  gatewayIdFocus = false;
  gatewayNameFocus = false;
  gatewayDescriptionFocus = false;
  gatewayEndpointUrlFocus = false;

  deleteModalOpen = false;
  selectedGateway: Gateway = {
    id: "",
    friendlyName: "",
    description: "",
    endpointUrl: "",
    dateCreated: new Date().toLocaleString(),
  }
  affectedSchedules = 0;

  constructor(rootStore: RootStore) {
    super(rootStore);
    makeObservable(this, {
      gatewayIdFocus: observable,
      gatewayNameFocus: observable,
      gatewayDescriptionFocus: observable,
      gatewayEndpointUrlFocus: observable,
      deleteModalOpen: observable,
      selectedGateway: observable,
      affectedSchedules: observable,
      setGatewayIdFocus: action,
      setGatewayNameFocus: action,
      setGatewayDescriptionFocus: action,
      setGatewayEndpointUrlFocus: action,
      setDeleteModalOpen: action,
      setSelectedGateway: action,
      setAffectedSchedules: action,
    });
  }

  setGatewayIdFocus = (focus: boolean) => {
    action(() => {
      this.gatewayIdFocus = focus;
    })();
  }


  setGatewayNameFocus = (focus: boolean) => {
    action(() => {
      this.gatewayNameFocus = focus;
    })();
  }

  setGatewayDescriptionFocus = (focus: boolean) => {
    action(() => {
      this.gatewayDescriptionFocus = focus;
    })();
  }

  setGatewayEndpointUrlFocus = (focus: boolean) => {
    action(() => {
      this.gatewayEndpointUrlFocus = focus;
    })();
  }


  setDeleteModalOpen = (open: boolean) => {
    action(() => {
      this.deleteModalOpen = open;
    })();
  }

  setSelectedGateway = (gateway: Gateway) => {
    action(() => {
      this.selectedGateway = gateway;
    })();
  }

  setAffectedSchedules = (schedules: number) => {
    action(() => {
      this.affectedSchedules = schedules;
    })();
  }
}
