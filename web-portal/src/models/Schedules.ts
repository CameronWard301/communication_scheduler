
export enum ScheduleStatus {
  Paused = "Paused",
  Running = "Running"
}

export interface ScheduleTableItem {
  id: string;
  status: ScheduleStatus;
  gatewayName: string
  gatewayId: string;
  userId: string;
  createdAt: string;
  updatedAt: string;
  nextRun: string;
  lastRun: string;
}

export interface SchedulePage {
  schedules: ScheduleTableItem[];
  totalElements: number;
  pageNumber: number;
  pageSize: number;
}
