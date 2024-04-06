import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {
  CalendarMonthSpec,
  CalendarWeekSpec,
  IntervalSpec,
  Schedule,
  ScheduleStatus,
  ScheduleType
} from "../models/Schedules.ts";
import {GridRowSelectionModel} from "@mui/x-data-grid";
import {Gateway} from "../models/Gateways.ts";

export class ScheduleEditStore {
  rootStore: RootStore;

  serverSchedule: Schedule | null = null;
  updatedSchedule: Schedule | null = null;
  updateScheduleSpec: null | {
    scheduleType: ScheduleType
    intervalSpec: IntervalSpec,
    calendarWeekSpec: CalendarWeekSpec,
    calendarMonthSpec: CalendarMonthSpec,
    cronSpec: string
  } = null
  gatewaySelectionModel: GridRowSelectionModel = [];

  loading = false;
  changeGatewayModalOpen = false;
  changeScheduleModalOpen = false;
  deleteScheduleModalOpen = false;
  confirmPauseModalOpen = false;
  confirmResumeModalOpen = false;
  confirmUpdateModalOpen = false;

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this)
  }

  setServerSchedule = (schedule: Schedule) => {
    action(() => {
      this.serverSchedule = schedule;
      this.updatedSchedule = schedule;
    })();
  }

  setLoading = (loading: boolean) => {
    action(() => {
      this.loading = loading;
    })();
  }

  reset = () => {
    action(() => {
      this.serverSchedule = null;
      this.updatedSchedule = null;
      this.updateScheduleSpec = null;
      this.gatewaySelectionModel = [];
      this.loading = false;
      this.changeGatewayModalOpen = false;
      this.changeScheduleModalOpen = false;
      this.deleteScheduleModalOpen = false;
      this.confirmPauseModalOpen = false;
      this.confirmResumeModalOpen = false;
    })();
  }

  isUserIdValid = () => {
    return this.updatedSchedule?.userId !== "";
  }

  setUserId = (userId: string) => {
    action(() => {
      this.updatedSchedule!.userId = userId;
    })();
  }

  setChangeGatewayModalOpen = (open: boolean) => {
    action(() => {
      this.changeGatewayModalOpen = open;
    })();
  }

  setGatewaySelectionModel = (selectionModel: GridRowSelectionModel) => {
    action(() => {
      this.gatewaySelectionModel = selectionModel;
    })();
  }

  setUpdatedGateway = (gateway: Gateway) => {
    if (gateway === undefined) {
      action(() => {
        this.updatedSchedule!.gateway = this.serverSchedule!.gateway;
        this.updatedSchedule!.gatewayId = this.serverSchedule!.gatewayId;
        this.updatedSchedule!.gatewayName = this.serverSchedule!.gatewayName;
      })();
      return
    }
    action(() => {
      this.updatedSchedule!.gateway = gateway;
      this.updatedSchedule!.gatewayId = gateway.id;
      this.updatedSchedule!.gatewayName = gateway.friendlyName;
    })();
  }

  setChangeScheduleModalOpen = (open: boolean) => {
    action(() => {
      this.changeScheduleModalOpen = open;
    })();
  }

  setDeleteScheduleModalOpen = (open: boolean) => {
    action(() => {
      this.deleteScheduleModalOpen = open;
    })();
  }

  setConfirmPauseModalOpen = (open: boolean) => {
    action(() => {
      this.confirmPauseModalOpen = open;
    })();
  }

  setConfirmResumeModalOpen = (open: boolean) => {
    action(() => {
      this.confirmResumeModalOpen = open;
    })();
  }

  setConfirmUpdateModalOpen = (open: boolean) => {
    action(() => {
      this.confirmUpdateModalOpen = open;
    })();
  }

  setStatus = (status: ScheduleStatus) => {
    action(() => {
      if (this.updatedSchedule !== null && this.serverSchedule !== null) {
        this.updatedSchedule.status = status;
        this.serverSchedule.status = status;
      }
    })();
  }

  setUpdatingScheduleSpec = (scheduleType: ScheduleType, intervalSpec: IntervalSpec, calendarWeekSpec: CalendarWeekSpec, calendarMonthSpec: CalendarMonthSpec, cronSpec: string) => {
    action(() => {
      this.updateScheduleSpec = {
        scheduleType: scheduleType,
        intervalSpec: intervalSpec,
        calendarWeekSpec: calendarWeekSpec,
        calendarMonthSpec: calendarMonthSpec,
        cronSpec: cronSpec
      }
    })();
  }

  hasUserIdChanged = () => {
    return this.serverSchedule?.userId !== this.updatedSchedule?.userId;
  }

  hasGatewayChanged = () => {
    return this.serverSchedule?.gatewayId !== this.updatedSchedule?.gatewayId;
  }

  hasScheduleChanged = () => {
    return this.updateScheduleSpec !== null;
  }


}
