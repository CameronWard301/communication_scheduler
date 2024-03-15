import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {IntervalSpec, Period} from "../models/Schedules.ts";

export class CreateScheduleStore {
  rootStore: RootStore;

  intervalDays = "0"
  intervalHours = "0"
  intervalMinutes = "0"
  intervalSeconds = "0"
  intervalOffset = "0"
  intervalOffsetTime = "S"

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this);
  }

  getNumericValueForField = (fieldValue: string): string => {
    if (fieldValue === "") {
      return "";
    }
    fieldValue = fieldValue.replace('.', '');
    let value = parseInt(fieldValue);
    if (value < 0) {
      value = 0;
    }
    return value.toString();
  }

  setIntervalDays = (days: string) => {
    action(() => {
      this.intervalDays = this.getNumericValueForField(days);
    })();
  }

  setIntervalHours = (hours: string) => {
    action(() => {
      this.intervalHours = this.getNumericValueForField(hours);
    })();
  }

  setIntervalMinutes = (minutes: string) => {
    action(() => {
      this.intervalMinutes = this.getNumericValueForField(minutes);
    })();
  }

  setIntervalSeconds = (seconds: string) => {
    action(() => {
      this.intervalSeconds = this.getNumericValueForField(seconds);
    })();
  }

  setIntervalOffset = (offset: string) => {
    action(() => {
      this.intervalOffset = this.getNumericValueForField(offset)
    })();
  }

  setIntervalOffsetTime = (offsetTime: string) => {
    action(() => {
      this.intervalOffsetTime = offsetTime;
    })();
  }

  isIntervalFieldsValid = (): boolean => {
    return this.intervalDays !== "" && this.intervalHours !== "" && this.intervalMinutes !== "" && this.intervalSeconds !== "" && this.intervalOffset !== "";
  }

  isIntervalFieldsAllZero = (): boolean => {
    return this.intervalDays === "0" && this.intervalHours === "0" && this.intervalMinutes === "0" && this.intervalSeconds === "0";
  }

  isIntervalOffsetValid = (): boolean => {
    let offsetSeconds = parseInt(this.intervalOffset);
    switch (this.intervalOffsetTime) {
      case "S":
        break;
      case "M":
        offsetSeconds = offsetSeconds * 60;
        break
      case "H":
        offsetSeconds = offsetSeconds * 3600;
        break;
      case "D":
        offsetSeconds = offsetSeconds * 86400;
        break;
    }

    const intervalSeconds = (parseInt(this.intervalDays) * 86400) + (parseInt(this.intervalHours) * 3600) + (parseInt(this.intervalMinutes) * 60) + parseInt(this.intervalSeconds);
    return offsetSeconds < intervalSeconds;
  }

  getIntervalObject = (): IntervalSpec => {
    return {
      days: this.intervalDays,
      hours: this.intervalHours,
      minutes: this.intervalMinutes,
      seconds: this.intervalSeconds,
      offset: this.intervalOffset,
      offsetPeriod: this.intervalOffsetTime as Period
    }
  }
}
