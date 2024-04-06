import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {GridRowSelectionModel} from "@mui/x-data-grid";
import {
  CalendarMonthSpec,
  CalendarWeekSpec,
  DayOfWeek,
  IntervalSpec,
  Month,
  Period,
  ScheduleType
} from "../models/Schedules.ts";
import {Gateway} from "../models/Gateways.ts";

export class GenerateScheduleStore {
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

  calendarWeekSpec: CalendarWeekSpec = {
    dayOfWeek: DayOfWeek.Monday,
    hours: 0,
    minutes: 0
  }

  calendarMonthSpec: CalendarMonthSpec = {
    dayOfMonth: 1,
    month: Month.January,
    hours: 0,
    minutes: 0
  }

  cronString = "* * * * *"

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

  setCalendarWeekSpec = (calendarWeekSpec: CalendarWeekSpec) => {
    action(() => {
      this.calendarWeekSpec = calendarWeekSpec;
    })();
  }

  setCalendarMonthSpec = (calendarMonthSpec: CalendarMonthSpec) => {
    action(() => {
      this.calendarMonthSpec = calendarMonthSpec;
    })();
  }

  setCronString = (cronString: string) => {
    action(() => {
      this.cronString = cronString;
    })();
  }

  reset = () => {
    action(() => {
      this.allowNext = false;
      this.currentStep = 0;
      this.gatewaySelectionModel = [];
      this.userId = "";
      this.loading = false;
      this.selectedGateway = {
        id: "",
        friendlyName: "",
        description: "",
        endpointUrl: "",
        dateCreated: new Date().toLocaleString(),
      }
      this.selectedScheduleType = ScheduleType.Interval
      this.intervalSpec = {
        days: "0",
        hours: "0",
        minutes: "0",
        seconds: "0",
        offset: "0",
        offsetPeriod: Period.S
      }
      this.calendarWeekSpec = {
        dayOfWeek: DayOfWeek.Monday,
        hours: 0,
        minutes: 0
      }
      this.calendarMonthSpec = {
        dayOfMonth: 1,
        month: Month.January,
        hours: 0,
        minutes: 0
      }
      this.cronString = "* * * * *"
    })();
  }

}
