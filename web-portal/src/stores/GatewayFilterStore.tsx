import {RootStore} from "./RootStore.tsx";
import {action, makeObservable, observable} from "mobx";
import {GatewayPageStore} from "./GatewayPageStore.tsx";
import {GridRowSelectionModel} from "@mui/x-data-grid";

export class GatewayFilterStore extends GatewayPageStore {
  rowSelectionModel: GridRowSelectionModel = [];

  constructor(rootStore: RootStore) {
    super(rootStore);
    makeObservable(this, {
      rowSelectionModel: observable,
      setRowSelectionModel: action,
      resetFilter: action,
    });
    this.paginationModel = {
      pageSize: 5,
      page: 0,
    };
  }

  setRowSelectionModel = (rowSelectionModel: GridRowSelectionModel) => {
    action(() => {
      this.rowSelectionModel = rowSelectionModel;
    })();
  }

  resetFilter = () => {
    this.resetFilters();
    action(() => {
      this.rowSelectionModel = [];
    })();
  }

}
