import {RootStore} from "./RootStore.tsx";
import {GridPaginationModel, GridRowSelectionModel, GridSortModel} from "@mui/x-data-grid";
import {action, makeAutoObservable} from "mobx";
import {ScheduleTableItem} from "../models/Schedules.ts";

export class ScheduleTableStore {
  rootStore: RootStore;
  paginationModel: GridPaginationModel = {
    pageSize: 25,
    page: 0,
  };
  sortModel: GridSortModel = [];
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

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this);
  }

  setPaginationModel = (paginationModel: GridPaginationModel) => {
    action(() => {
      this.paginationModel = paginationModel;
    })();
  }

  setSortModel = (sortModel: GridSortModel) => {
    action(() => {
      this.sortModel = sortModel;
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
      console.log(selectionModel);
      this.checkBoxSelectionModel = selectionModel;
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

}
