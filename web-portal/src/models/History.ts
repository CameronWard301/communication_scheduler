import {Theme} from "@mui/material/styles";

export type CommunicationStatus =
  "Any Status"
  | "Completed"
  | "Running"
  | "Failed"
  | "Terminated"
  | "Cancelled"
  | "Unknown"

export const ValidStatus: CommunicationStatus[] = ["Any Status", "Completed", "Running", "Failed", "Terminated", "Cancelled", "Unknown"];


export interface HistoryItem {
  id: string; // AKA Run ID in temporal
  workflowId: string;
  userId: string;
  gatewayId: string;
  gatewayName: string;
  scheduleId: string;
  status: CommunicationStatus;
  startTime: string;
  endTime: string | null;
}

export interface HistoryPageQuery {
  pageSize?: string;
  pageNumber?: string;
  status?: CommunicationStatus;
  gatewayId?: string;
  scheduleId?: string;
  userId?: string;
}

export interface HistoryPage {
  historyItems: HistoryItem[];
  totalElements: number;
  pageSize: number;
  pageNumber: number;
}

export const getStatusColour = (status: CommunicationStatus, theme: Theme) => {
  switch (status) {
    case "Completed":
      return theme.palette.success.dark;
    case "Running":
      return theme.palette.primary.main;
    case "Failed":
      return theme.palette.error.main;
    case "Terminated":
      return theme.palette.warning.dark;
    case "Cancelled":
      return theme.palette.warning.light;
    case "Unknown":
      return theme.palette.info.main;
    default:
      return theme.palette.info.main;
  }
}
