
export enum ScheduleStatus {
  Paused = "Paused",
  Running = "Running"
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
