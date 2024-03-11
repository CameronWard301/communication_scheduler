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

export interface IntervalSpec {
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
  year: CalendarPeriod[]
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
      intervals: IntervalSpec[],
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
  interval: IntervalSpec;
  cronExpression: string;
}

export interface ClientScheduleCreateEdit {
  id: string;
  status: ScheduleStatus;
  gatewayId: string;
  userId: string;
  gatewayName: string;
  calendar: CalendarSpec;
  interval: IntervalSpec;
  cronExpression: string;
  nextRun: string;
  lastRun: string;
}
