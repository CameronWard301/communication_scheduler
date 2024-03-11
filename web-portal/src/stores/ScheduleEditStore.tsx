import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {Schedule, ScheduleStatus} from "../models/Schedules.ts";

export class ScheduleEditStore {
  rootStore: RootStore;

  serverSchedule: Schedule = {
    id: "",
    gatewayId: "",
    userId: "",
    nextRun: "",
    lastRun: "",
    status: ScheduleStatus.Paused,
    gatewayName: "",
    createdAt: "",
    updatedAt: "",
    nextActionTimes: [],
  }

  loading = false;

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this)
  }

  setServerSchedule = (schedule: Schedule) => {
    action(() => {
      this.serverSchedule = schedule;
    })();
  }

  setLoading = (loading: boolean) => {
    action(() => {
      this.loading = loading;
    })();
  }


}
