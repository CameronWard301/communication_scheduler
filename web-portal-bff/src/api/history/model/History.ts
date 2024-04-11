export type ClientCommunicationStatus =
  "Any Status"
  | "Completed"
  | "Running"
  | "Failed"
  | "Terminated"
  | "Cancelled"
  | "Unknown";

export const getStatusNumberByString = (status: ClientCommunicationStatus): number => {
  switch (status) {
    case "Running":
      return 1;
    case "Completed":
      return 2;
    case "Failed":
      return 3;
    case "Cancelled":
      return 4;
    case "Terminated":
      return 5;
    default:
      return 0;
  }
};

export const getStatusStringByNumber = (status: number): ClientCommunicationStatus => {
  switch (status) {
    case 1:
      return "Running";
    case 2:
      return "Completed";
    case 3:
      return "Failed";
    case 4:
      return "Cancelled";
    case 5:
      return "Terminated";
    default:
      return "Unknown";
  }

};

export interface ClientHistoryItem {
  workflowId: string;
  id: string;
  userId: string;
  gatewayId: string;
  gatewayName: string;
  scheduleId: string;
  status: ClientCommunicationStatus;
  startTime: string;
  endTime: string | null;
}

export interface ClientHistoryPageQuery {
  pageSize?: string;
  pageNumber?: string;
  status?: ClientCommunicationStatus;
  gatewayId?: string;
  scheduleId?: string;
  userId?: string;
}

export interface ServerHistoryPageQuery {
  pageSize?: string;
  pageNumber?: string;
  status?: number;
  gatewayId?: string;
  scheduleId?: string;
  userId?: string;
}

export interface ClientHistoryPage {
  historyItems: ClientHistoryItem[];
  totalElements: number;
  pageSize: number;
  pageNumber: number;
}

export interface ServerHistoryPage {
  content: ServerHistoryItem[];
  totalElements: number;
  size: number;
  number: number;
}

export interface HistoryTimestamp {
  seconds: number;
  nanos: number;
}

export interface ServerHistoryItem {
  workflowId: string;
  runId: string;
  userId: string;
  gatewayId: string;
  scheduleId: string;
  type: string;
  startTime: HistoryTimestamp;
  endTime?: HistoryTimestamp;
  taskQueue: string;
  status: number;
}

export type StopCommunicationResponse = null | string;
