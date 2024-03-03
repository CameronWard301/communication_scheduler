import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {GridPaginationModel, GridSortModel} from "@mui/x-data-grid";
import {Gateway} from "../models/Gateways.ts";

export class GatewayTableStore {
  rootStore: RootStore;
  paginationModel: GridPaginationModel = {
    pageSize: 25,
    page: 0,
  };
  sortModel: GridSortModel = [];
  gatewayTableData: Gateway[] = [];
  isLoading = false;
  totalCount = 0;

  gatewayIdFocus = false;
  gatewayIdFilter = "";

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
      console.log(sortModel);
      this.sortModel = sortModel;
    })();
  }

  setLoading = (loading: boolean) => {
    action(() => {
      this.isLoading = loading;
    })();
  }

  setTotalCount = (totalCount: number) => {
    action(() => {
      this.totalCount = totalCount;
    })();
  }

  setGatewayTableData = (data: Gateway[]) => {
    action(() => {
      this.gatewayTableData = data;
    })();
  }

  setGatewayIdFocus = (focus: boolean) => {
    action(() => {
      this.gatewayIdFocus = focus;
    })();
  }

  setGatewayIdFilter = (filter: string) => {
    action(() => {
      console.log(filter);
      this.gatewayIdFilter = filter;
    })();
  }
}
