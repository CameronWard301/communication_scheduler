import {RootStore} from "./RootStore.tsx";
import {GridPaginationModel, GridSortModel} from "@mui/x-data-grid";
import {Gateway} from "../models/Gateways.ts";
import {action, makeObservable, observable} from "mobx";

export class GatewayPageStore {
  rootStore: RootStore;
  paginationModel: GridPaginationModel = {
    pageSize: 25,
    page: 0,
  };
  sortModel: GridSortModel = [];
  gatewayTableData: Gateway[] = [];
  isLoading = false;
  totalCount = 0;


  gatewayNameFilter = "";
  gatewayIdFilter = "";
  gatewayDescriptionFilter = "";
  gatewayEndpointUrlFilter = "";

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeObservable(this, {
      paginationModel: observable,
      sortModel: observable,
      gatewayTableData: observable,
      isLoading: observable,
      totalCount: observable,
      gatewayNameFilter: observable,
      gatewayIdFilter: observable,
      gatewayDescriptionFilter: observable,
      gatewayEndpointUrlFilter: observable,
      setPaginationModel: action,
      setSortModel: action,
      setLoading: action,
      setGatewayTableData: action,
      setTotalCount: action,
      setGatewayNameFilter: action,
      setGatewayIdFilter: action,
      setGatewayDescriptionFilter: action,
      setGatewayEndpointUrlFilter: action,
      resetFilters: action,
    });
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

  setGatewayTableData = (data: Gateway[]) => {
    action(() => {
      this.gatewayTableData = data;
    })();
  }

  setTotalCount = (count: number) => {
    action(() => {
      this.totalCount = count;
    })();
  }

  setGatewayNameFilter = (name: string) => {
    action(() => {
      this.gatewayNameFilter = name;
    })();
  }

  setGatewayIdFilter = (id: string) => {
    action(() => {
      this.gatewayIdFilter = id;
    })();
  }

  setGatewayDescriptionFilter = (description: string) => {
    action(() => {
      this.gatewayDescriptionFilter = description;
    })();
  }

  setGatewayEndpointUrlFilter = (url: string) => {
    action(() => {
      this.gatewayEndpointUrlFilter = url;
    })();
  }

  resetFilters = () => {
    action(() => {
      this.gatewayNameFilter = "";
      this.gatewayIdFilter = "";
      this.gatewayDescriptionFilter = "";
      this.gatewayEndpointUrlFilter = "";
    })();
  }


}
