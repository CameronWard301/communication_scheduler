import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {GridRowSelectionModel} from "@mui/x-data-grid";
import {IntervalSpec, Period, ScheduleType} from "../models/Schedules.ts";
import {Gateway} from "../models/Gateways.ts";

export class ScheduleAddStore {
  rootStore: RootStore;

  allowNext = false;
  currentStep = 0;

  gatewaySelectionModel: GridRowSelectionModel = [];

  userId = "";
  loading = false;
  selectedGateway: Gateway = {
    id: "",
    friendlyName: "",
    description: "",
    endpointUrl: "",
    dateCreated: new Date().toLocaleString(),
  }
  selectedScheduleType = ScheduleType.Interval

  intervalSpec: IntervalSpec = {
    days: "0",
    hours: "0",
    minutes: "0",
    seconds: "0",
    offset: "0",
    offsetPeriod: Period.S
  }

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

  setUserId = (userId: string) => {
    action(() => {
      this.userId = userId;
    })();
  }

  isUserIdValid = () => {
    return this.userId !== "";
  }

  setGatewaySelectionModel = (selectionModel: GridRowSelectionModel) => {
    action(() => {
      this.gatewaySelectionModel = selectionModel;
    })();
  }

  setSelectedScheduleType = (scheduleType: ScheduleType) => {
    action(() => {
      this.selectedScheduleType = scheduleType;
    })();
  }

  setIntervalSpec = (intervalSpec: IntervalSpec) => {
    action(() => {
      this.intervalSpec = intervalSpec;
    })();
  }

  setLoading = (isLoading: boolean) => {
    action(() => {
      this.loading = isLoading;
    })();
  }

  setSelectedGateway = (gateway: Gateway) => {
    action(() => {
      this.selectedGateway = gateway;
    })();
  }

}
