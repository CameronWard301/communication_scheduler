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

  gatewayNameFocus = false;
  gatewayNameFilter = "";

  gatewayDescriptionFocus = false;
  gatewayDescriptionFilter = "";

  gatewayEndpointUrlFocus = false;
  gatewayEndpointUrlFilter = "";

  deleteModalOpen = false;
  selectedGateway: Gateway = {
    id: "",
    friendlyName: "",
    description: "",
    endpointUrl: "",
    dateCreated: new Date().toLocaleString(),
  }
  affectedSchedules = 0;

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
      this.gatewayIdFilter = filter;
    })();
  }

  setGatewayNameFocus = (focus: boolean) => {
    action(() => {
      this.gatewayNameFocus = focus;
    })();
  }

  setGatewayNameFilter = (filter: string) => {
    action(() => {
      this.gatewayNameFilter = filter;
    })();
  }

  setGatewayDescriptionFocus = (focus: boolean) => {
    action(() => {
      this.gatewayDescriptionFocus = focus;
    })();
  }

  setGatewayDescriptionFilter = (filter: string) => {
    action(() => {
      this.gatewayDescriptionFilter = filter;
    })();
  }

  setGatewayEndpointUrlFocus = (focus: boolean) => {
    action(() => {
      this.gatewayEndpointUrlFocus = focus;
    })();
  }

  setGatewayEndpointUrlFilter = (filter: string) => {
    action(() => {
      this.gatewayEndpointUrlFilter = filter;
    })();
  }

  resetFilters = () => {
    action(() => {
      this.gatewayIdFilter = "";
      this.gatewayNameFilter = "";
      this.gatewayDescriptionFilter = "";
      this.gatewayEndpointUrlFilter = "";
    })();
  }

  setDeleteModalOpen = (open: boolean) => {
    action(() => {
      this.deleteModalOpen = open;
    })();
  }

  setSelectedGateway = (gateway: Gateway) => {
    action(() => {
      this.selectedGateway = gateway;
    })();
  }

  setAffectedSchedules = (schedules: number) => {
    action(() => {
      this.affectedSchedules = schedules;
    })();
  }
}
