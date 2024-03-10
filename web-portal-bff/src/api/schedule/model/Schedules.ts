export interface ScheduleQueryParams {
  userId?: string;
  gatewayId?: string;
}

export interface SchedulePageQueryParams extends ScheduleQueryParams {
  page: string;
  pageSize: string;
}
