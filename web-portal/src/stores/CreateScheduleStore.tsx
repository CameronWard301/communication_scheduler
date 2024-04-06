import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {
  CalendarMonthSpec,
  CalendarWeekSpec,
  DayOfWeek,
  getMonthNumber,
  IntervalSpec,
  Month,
  Period
} from "../models/Schedules.ts";
import dayjs, {Dayjs} from "dayjs";

export class CreateScheduleStore {
  rootStore: RootStore;

  intervalDays = "0"
  intervalHours = "0"
  intervalMinutes = "0"
  intervalSeconds = "0"
  intervalOffset = "0"
  intervalOffsetTime = "S"

  dayOfWeekType = DayOfWeek.Monday;
  scheduleTime: Dayjs = dayjs().startOf("day");
  scheduleTimeError = false;

  dayOfMonth = "1";
  monthType: Month = Month.January;

  cronString = "* * * * *"
  cronParseError = false;

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

  setDayOfMonth = (dayOfMonth: string) => {
    action(() => {
      this.dayOfMonth = this.getNumericValueForField(dayOfMonth);
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

  setDayOfWeekType = (dayOfWeekType: DayOfWeek) => {
    action(() => {
      this.dayOfWeekType = dayOfWeekType;
    })();
  }

  setScheduleTime = (time: Dayjs | null) => {
    if (time === null) {
      this.setScheduleTimeError(true);
      return;
    }
    this.setScheduleTimeError(false);
    action(() => {
      this.scheduleTime = time;
    })();
  }

  getCalendarWeekObject = (): CalendarWeekSpec => {
    return {
      dayOfWeek: this.dayOfWeekType,
      hours: this.scheduleTime.hour(),
      minutes: this.scheduleTime.minute()
    }
  }

  isDayOfMonthValid = (): boolean => {
    return this.dayOfMonth !== "" && (parseInt(this.dayOfMonth) > 0 && parseInt(this.dayOfMonth) < 32);
  }

  setMonthType = (monthType: Month) => {
    action(() => {
      this.monthType = monthType;
    })();
  }

  isMonthTypeValid = (): boolean => {
    if (this.monthType === Month.EveryMonth) {
      return true;
    }
    const year = dayjs().get('year')
    return dayjs(year.toString() + '-' + (getMonthNumber(this.monthType) + 1).toString() + '-' + this.dayOfMonth, "YYYY-M-D", true).isValid();
  }

  getCalendarMonthObject = (): CalendarMonthSpec => {
    return {
      dayOfMonth: parseInt(this.dayOfMonth),
      month: this.monthType,
      hours: this.scheduleTime.hour(),
      minutes: this.scheduleTime.minute()
    }
  }

  setCronString = (cronString: string) => {
    action(() => {
      this.cronString = cronString;
    })();
  }

  isCronStringValid = (): boolean => {
    const cronArray = this.cronString.split(" ");
    if (cronArray.length !== 5) {
      return false;
    }
    let isValid = true
    cronArray.forEach((value) => {
      if (value === "") {
        isValid = false;
      }
    })
    return isValid;
  }

  setCronParseError = (error: boolean) => {
    action(() => {
      this.cronParseError = error;
    })();
  }

  setScheduleTimeError = (error: boolean) => {
    action(() => {
      this.scheduleTimeError = error;
    })();
  }

  reset = () => {
    action(() => {
      this.intervalDays = "0"
      this.intervalHours = "0"
      this.intervalMinutes = "0"
      this.intervalSeconds = "0"
      this.intervalOffset = "0"
      this.intervalOffsetTime = "S"
      this.dayOfWeekType = DayOfWeek.Monday;
      this.scheduleTime = dayjs().startOf("day");
      this.dayOfMonth = "1";
      this.monthType = Month.January;
      this.cronString = "* * * * *"
      this.cronParseError = false;
    })();
  }


}
