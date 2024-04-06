import {RootStore} from "./RootStore";
import {action, makeAutoObservable} from "mobx";
import {BulkActionType} from "../models/Schedules.ts";
import {GridRowSelectionModel} from "@mui/x-data-grid";
import {Gateway} from "../models/Gateways.ts";

export class BulkActionStore {
  rootStore: RootStore;

  allowNext = false;
  currentStep = 1;
  isLoading = false;
  isUpdateInProgress = false;
  isConfirmModalOpen = false;

  bulkActionType: null | BulkActionType = null;
  gatewaySelectionModel: GridRowSelectionModel = []
  selectedGateway: Gateway | null = null;

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this);
  }

  setAllowNext = (allowNext: boolean) => {
    action(() => {
      this.allowNext = allowNext;
    })();
  }

  incrementStep = () => {
    action(() => {
      this.currentStep++;
    })();
  }

  decrementStep = () => {
    action(() => {
      this.currentStep--;
    })();
  }

  setCurrentStep = (step: number) => {
    action(() => {
      this.currentStep = step;
    })();
  }

  setBulkActionType = (type: BulkActionType) => {
    action(() => {
      this.bulkActionType = type;
    })();
  }

  setLoading = (loading: boolean) => {
    action(() => {
      this.isLoading = loading;
    })();
  }

  setConfirmModalOpen = (isOpen: boolean) => {
    action(() => {
      this.isConfirmModalOpen = isOpen;
    })();
  }

  setGatewaySelectionModel = (selectionModel: GridRowSelectionModel) => {
    action(() => {
      this.gatewaySelectionModel = selectionModel;
    })();
  }

  setSelectedGateway = (gateway: Gateway) => {
    action(() => {
      this.selectedGateway = gateway;
    })();
  }

  setUpdateInProgress = (isUpdateInProgress: boolean) => {
    action(() => {
      this.isUpdateInProgress = isUpdateInProgress;
    })();
  }

  reset = () => {
    action(() => {
      this.allowNext = false;
      this.currentStep = 1;
      this.isLoading = false;
      this.isConfirmModalOpen = false;
      this.bulkActionType = null;
    })();
  }
}
