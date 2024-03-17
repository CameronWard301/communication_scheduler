export interface ScheduleQueryParams {
  userId?: string;
  gatewayId?: string;
  scheduleId?: string;
}

export interface SchedulePageQueryParams extends ScheduleQueryParams {
  page: string;
  pageSize: string;
}

export enum ScheduleStatus {
  Paused = "Paused",
  Running = "Running"
}

export interface ActionRun {
  scheduledAt: string;
  runId: string;
}

export interface ServerIntervalSpec {
  every: string
  offset: string
}

export interface CalendarPeriod {
  start: string;
  end: string;
  step: string;
}

export interface CalendarSpec {
  seconds: CalendarPeriod[]
  minutes: CalendarPeriod[]
  hour: CalendarPeriod[]
  dayOfMonth: CalendarPeriod[]
  month: CalendarPeriod[],
  year: CalendarPeriod[] | []
  dayOfWeek: CalendarPeriod[]
  comment: string
}

export interface Schedule {
  scheduleId: string;
  info: {
    numActions: number;
    numActionsMissedCatchupWindow: number;
    numActionsSkippedOverlap: number;
    runningActions: ActionRun[];
    recentActions: ActionRun[];
    nextActionTimes: string[];
    createdAt: string;
    updatedAt: string;
  },
  schedule: {
    state: {
      note: string
      paused: boolean
      limitedAction: boolean
      remainingActions: number
    },
    spec: {
      intervals: ServerIntervalSpec[],
      calendars: CalendarSpec[],
      cronExpressions: string[]
    }
  },
  searchAttributes: {
    userId: string[];
    gatewayId: string[];
    scheduleId: string[];
  }

}

export interface BaseClientSchedule {
  id: string;
  status: ScheduleStatus;
  gatewayName: string
  gatewayId: string;
  userId: string;
  nextRun: string;
  lastRun: string;
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

export enum DayOfWeek {
  EveryDay = "EveryDay",
  Weekdays = "Weekdays",
  Weekend = "Weekend",
  Monday = "Monday",
  Tuesday = "Tuesday",
  Wednesday = "Wednesday",
  Thursday = "Thursday",
  Friday = "Friday",
  Saturday = "Saturday",
  Sunday = "Sunday",
}

export interface ClientIntervalSpec {
  days: string;
  hours: string;
  minutes: string;
  seconds: string;
  offset: string;
  offsetPeriod: Period;
}

export interface ClientCalendarWeekSpec {
  dayOfWeek: DayOfWeek;
  hours: string;
  minutes: string;
}


export interface ClientScheduleCreateRequest {
  gatewayId: string;
  userId: string;
  scheduleType: ScheduleType;
  intervalSpec?: ClientIntervalSpec;
  calendarWeekSpec?: ClientCalendarWeekSpec;
  cronSpec?: string;
}

export interface ServerScheduleCreateRequest {
  gatewayId: string;
  userId: string;
  paused: boolean;
  interval?: ServerIntervalSpec;
  calendar?: CalendarSpec;
  cronExpression?: string;
}

export interface ScheduleTableItem extends BaseClientSchedule {
}

export interface ClientSchedule extends BaseClientSchedule {
  createdAt: string;
  updatedAt: string;
  nextActionTimes: string[];
  recentActions: string[];
}

export interface ServerSchedulePage {
  content: Schedule[];
  totalElements: number;
  size: number;
  number: number;
}

export interface ClientSchedulePage {
  schedules: ScheduleTableItem[];
  totalElements: number;
  pageSize: number;
  pageNumber: number;
}

export interface ServerScheduleCreateEdit {
  scheduleId: string;
  gatewayId: string;
  userId: string;
  paused: boolean;
  calendar: CalendarSpec;
  interval: ServerIntervalSpec;
  cronExpression: string;
}

export interface ClientScheduleCreateEdit {
  id: string;
  status: ScheduleStatus;
  gatewayId: string;
  userId: string;
  gatewayName: string;
  calendar: CalendarSpec;
  interval: ServerIntervalSpec;
  cronExpression: string;
  nextRun: string;
  lastRun: string;
}
