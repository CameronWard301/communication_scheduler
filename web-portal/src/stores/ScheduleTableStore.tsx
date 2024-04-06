import {RootStore} from "./RootStore.tsx";
import {GridPaginationModel, GridRowSelectionModel} from "@mui/x-data-grid";
import {action, makeAutoObservable} from "mobx";
import {Schedule, ScheduleStatus, ScheduleTableItem} from "../models/Schedules.ts";

export class ScheduleTableStore {
  rootStore: RootStore;
  paginationModel: GridPaginationModel = {
    pageSize: 25,
    page: 0,
  };
  scheduleTableData: ScheduleTableItem[] = [];
  isLoading = false;
  totalCount = 0;
  checkBoxSelectionModel: GridRowSelectionModel = [];
  selectedAll = false;

  scheduleIdFilter = "";
  scheduleIdFocus = false;

  gatewayIdFilter = "";
  gatewayIdFocus = false;

  userIdFilter = "";
  userIdFocus = false;

  confirmPauseModalOpen = false;
  confirmResumeModalOpen = false;
  confirmDeleteModalOpen = false;

  selectedSchedule: Schedule = {
    id: "",
    gatewayId: "",
    userId: "",
    nextRun: "",
    lastRun: "",
    status: ScheduleStatus.Running,
    gatewayName: "",
    gateway: {
      id: "",
      friendlyName: "",
      description: "",
      endpointUrl: "",
      dateCreated: new Date().toLocaleString(),
    },
    createdAt: "",
    updatedAt: "",
    nextActionTimes: [],
  }

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this);
  }

  setPaginationModel = (paginationModel: GridPaginationModel) => {
    action(() => {
      this.paginationModel = paginationModel;
    })();
  }

  setLoading = (loading: boolean) => {
    action(() => {
      this.isLoading = loading;
    })();
  }

  setScheduleTableData = (data: ScheduleTableItem[]) => {
    action(() => {
      this.scheduleTableData = data;
    })();
  }

  setTotalCount = (count: number) => {
    action(() => {
      this.totalCount = count;
    })();
  }

  setCheckBoxSelectionModel = (selectionModel: GridRowSelectionModel) => {
    action(() => {
      this.checkBoxSelectionModel = selectionModel;
    })();
  }

  addItemToCheckBoxSelectionModel = (id: string) => {
    action(() => {
      this.checkBoxSelectionModel.push(id);
    })();
  }

  removeItemFromCheckBoxSelectionModel = (id: string) => {
    action(() => {
      this.checkBoxSelectionModel = this.checkBoxSelectionModel.filter((selectedId) => selectedId !== id);
    })();
  }

  setSelectAll = (selected: boolean) => {
    action(() => {
      this.selectedAll = selected;
    })();
  }

  setScheduleIdFilter = (filter: string) => {
    action(() => {
      this.scheduleIdFilter = filter;
    })();
  }

  setScheduleIdFocus = (focus: boolean) => {
    action(() => {
      this.scheduleIdFocus = focus;
    })();
  }

  setUserIdFilter = (filter: string) => {
    action(() => {
      this.userIdFilter = filter;
    })();
  }

  setUserIdFocus = (focus: boolean) => {
    action(() => {
      this.userIdFocus = focus;
    })();
  }

  setGatewayIdFilter = (filter: string) => {
    action(() => {
      this.gatewayIdFilter = filter;
    })();
  }

  setGatewayIdFocus = (focus: boolean) => {
    action(() => {
      this.gatewayIdFocus = focus;
    })();
  }

  resetFilters = () => {
    action(() => {
      this.scheduleIdFilter = "";
      this.scheduleIdFocus = false;
      this.userIdFilter = "";
      this.userIdFocus = false;
      this.gatewayIdFilter = "";
      this.gatewayIdFocus = false;
    })();
  }

  resetSelection = () => {
    action(() => {
      this.checkBoxSelectionModel = [];
      this.selectedAll = false;
    })();
  }

  updateScheduleById = (schedule: Schedule) => {
    const index = this.scheduleTableData.findIndex((scheduleTable) => scheduleTable.id === schedule.id);
    if (index !== -1) {
      action(() => {
        this.scheduleTableData[index] = {
          id: schedule.id,
          gatewayId: schedule.gatewayId,
          userId: schedule.userId,
          nextRun: schedule.nextRun,
          lastRun: schedule.lastRun,
          status: schedule.status,
          gatewayName: schedule.gatewayName,
        }
      })();
    }
  }

  removeScheduleById = (id: string) => {
    const index = this.scheduleTableData.findIndex((scheduleTable) => scheduleTable.id === id);
    if (index !== -1) {
      action(() => {
        this.scheduleTableData.splice(index, 1);
      })();
    }
  }


  setConfirmPauseModalOpen = (open: boolean) => {
    action(() => {
      this.confirmPauseModalOpen = open;
    })();
  }

  setSelectedSchedule = (schedule: Schedule) => {
    action(() => {
      this.selectedSchedule = schedule;
    })();
  }

  setConfirmResumeModalOpen = (open: boolean) => {
    action(() => {
      this.confirmResumeModalOpen = open;
    })();
  }

  setConfirmDeleteModalOpen = (open: boolean) => {
    action(() => {
      this.confirmDeleteModalOpen = open;
    })();
  }


}
