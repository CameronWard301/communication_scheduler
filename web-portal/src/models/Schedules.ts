
export enum ScheduleStatus {
  Paused = "Paused",
  Running = "Running"
}

export enum ScheduleType {
  Interval = "Interval",
  CalendarWeek = "CalendarWeek",
  CalendarMonth = "CalendarMonth",
  Cron = "Cron"
}

export enum Period {
  S = "S",
  M = "M",
  H = "H",
  D = "D"
}

export interface IntervalSpec {
  days: string;
  hours: string;
  minutes: string;
  seconds: string;
  offset: string;
  offsetPeriod: Period;
}

export interface CreateScheduleRequest {
  gatewayId: string;
  userId: string;
  scheduleType: ScheduleType;
  intervalSpec?: IntervalSpec;
  calendarSpec?: string;
  cronSpec?: string;
}

export interface BaseSchedule {
  id: string;
  status: ScheduleStatus;
  gatewayName: string
  gatewayId: string;
  userId: string;
  nextRun: string;
  lastRun: string;
}

export interface ScheduleTableItem extends BaseSchedule {
  id: string;
  status: ScheduleStatus;
  gatewayName: string
  gatewayId: string;
  userId: string;
  nextRun: string;
  lastRun: string;
}

export interface Schedule extends BaseSchedule {
  createdAt: string;
  updatedAt: string;
  nextActionTimes: string[];
}

export interface SchedulePage {
  schedules: ScheduleTableItem[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
}
