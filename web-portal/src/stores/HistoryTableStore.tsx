import {CommunicationStatus, HistoryItem} from "../models/History.ts";
import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {GridPaginationModel} from "@mui/x-data-grid";

export class HistoryTableStore {
  rootStore: RootStore;
  statusFilter: CommunicationStatus[] = ["Any Status"];
  isStatusFilterFocused = false;

  historyTableData: HistoryItem[] = [];
  selectedHistoryItem: HistoryItem | null = null;

  gatewayIdFilter = "";
  gatewayIdFocus = false;

  scheduleIdFilter = "";
  scheduleIdFocus = false;

  userIdFilter = "";
  userIdFocus = false;

  totalCount = 0
  isLoading = false;

  openConfirmStopModal = false;
  openInspectModal = false;

  paginationModel: GridPaginationModel = {
    pageSize: 25,
    page: 0,
  };


  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this);
  }

  setStatusFilter = (status: CommunicationStatus[]) => {
    action(() => {
      this.statusFilter = status;
    })();
  }

  setIsStatusFilterFocused = (isFocused: boolean) => {
    action(() => {
      this.isStatusFilterFocused = isFocused;
    })();
  }

  removeStatusFromFilter = (status: CommunicationStatus) => {
    action(() => {
      this.statusFilter = this.statusFilter.filter((s) => s !== status);
    })();
  }


  isFilterError() {
    return this.statusFilter.length === 0;
  }

  resetFilters = () => {
    action(() => {
      this.statusFilter = ["Any Status"];
      this.gatewayIdFilter = "";
      this.scheduleIdFilter = "";
      this.userIdFilter = "";
      this.rootStore.gatewayFilterStore.resetFilter()
    })();
  }

  setGatewayIdFilter = (gatewayId: string) => {
    action(() => {
      this.gatewayIdFilter = gatewayId;
    })();
  }

  setGatewayIdFocus = (isFocused: boolean) => {
    action(() => {
      this.gatewayIdFocus = isFocused;
    })();
  }

  setScheduleIdFilter = (scheduleId: string) => {
    action(() => {
      this.scheduleIdFilter = scheduleId;
    })();
  }

  setScheduleIdFocus = (isFocused: boolean) => {
    action(() => {
      this.scheduleIdFocus = isFocused;
    })();
  }

  setUserIdFilter = (userId: string) => {
    action(() => {
      this.userIdFilter = userId;
    })();
  }

  setUserIdFocus = (isFocused: boolean) => {
    action(() => {
      this.userIdFocus = isFocused;
    })();
  }

  setPaginationModel = (paginationModel: GridPaginationModel) => {
    action(() => {
      this.paginationModel = paginationModel;
    })();
  }

  setHistoryTableData = (data: HistoryItem[]) => {
    action(() => {
      this.historyTableData = data;
    })();
  }

  setTotalCount = (count: number) => {
    action(() => {
      this.totalCount = count;
    })();
  }

  setLoading = (loading: boolean) => {
    action(() => {
      this.isLoading = loading;
    })();
  }

  setSelectedHistoryItem = (item: HistoryItem | null) => {
    action(() => {
      this.selectedHistoryItem = item;
    })();
  }

  setOpenConfirmStopModal = (open: boolean) => {
    action(() => {
      this.openConfirmStopModal = open;
    })();
  }

  setOpenInspectModal = (open: boolean) => {
    action(() => {
      this.openInspectModal = open;
    })();
  }

  updateHistoryItemStatus = (id: string) => {
    const index = this.historyTableData.findIndex((item) => item.id === id);
    if (index !== -1) {
      action(() => {
        this.historyTableData[index] = {
          ...this.historyTableData[index],
          status: "Terminated",
        }
        if (this.selectedHistoryItem && this.selectedHistoryItem.id === id) {
          this.selectedHistoryItem = {
            ...this.selectedHistoryItem,
            status: "Terminated",
          }
        }
      })();
    }


  }
}
