import {Gateway} from "./Gateways.ts";

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

export enum BulkActionType {
  Pause = "Pause",
  Resume = "Resume",
  Delete = "Delete",
  Gateway = "Update Gateway"
}

export enum BulkActionSelectionType {
  QUERY = "QUERY",
  IDs = "IDs"
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

export enum Month {
  EveryMonth = "Every Month",
  January = "January",
  February = "February",
  March = "March",
  April = "April",
  May = "May",
  June = "June",
  July = "July",
  August = "August",
  September = "September",
  October = "October",
  November = "November",
  December = "December",
}

export const getMonthNumber = (month: Month): number => {
  let monthNumber = 0;
  switch (month) {
    case Month.January:
      monthNumber = 0;
      break;
    case Month.February:
      monthNumber = 1;
      break;
    case Month.March:
      monthNumber = 2;
      break;
    case Month.April:
      monthNumber = 3;
      break;
    case Month.May:
      monthNumber = 4;
      break;
    case Month.June:
      monthNumber = 5;
      break;
    case Month.July:
      monthNumber = 6;
      break;
    case Month.August:
      monthNumber = 7;
      break;
    case Month.September:
      monthNumber = 8;
      break;
    case Month.October:
      monthNumber = 9;
      break;
    case Month.November:
      monthNumber = 10;
      break;
    case Month.December:
      monthNumber = 11;
      break;
  }
  return monthNumber;
}

export interface CalendarWeekSpec {
  dayOfWeek: DayOfWeek;
  hours: number;
  minutes: number;
}

export interface CalendarMonthSpec {
  dayOfMonth: number;
  month: Month;
  hours: number;
  minutes: number;
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
  calendarWeekSpec?: CalendarWeekSpec;
  calendarMonthSpec?: CalendarMonthSpec;
  cronSpec?: string;
}

export interface UpdateScheduleRequest extends CreateScheduleRequest {
  scheduleId: string;
}

export interface BulkUpdateScheduleRequest {
  paused?: boolean;
  gatewayId?: string;
  actionType: BulkActionType
  scheduleIds?: string;
}

export interface BulkUpdateResponse {
  success: boolean;
  totalModified: number;
  failureReasons: string[];
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
  gateway: Gateway;
  nextActionTimes: string[];
}

export interface SchedulePage {
  schedules: ScheduleTableItem[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
}
