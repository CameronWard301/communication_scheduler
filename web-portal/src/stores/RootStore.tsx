import {PreferencesStore} from "./PreferencesStore.tsx";
import {GatewayTableStore} from "./GatewayTableStore.tsx";
import {GatewayEditStore} from "./GatewayEditStore.tsx";
import {GatewayAddStore} from "./GatewayAddStore.tsx";
import {ScheduleTableStore} from "./ScheduleTableStore.tsx";
import {GatewayFilterStore} from "./GatewayFilterStore.tsx";
import {ScheduleEditStore} from "./ScheduleEditStore.tsx";
import {CreateScheduleStore} from "./CreateScheduleStore.tsx";
import {GenerateScheduleStore} from "./GenerateScheduleStore.tsx";
import {BulkActionStore} from "./BulkActionStore.tsx";
import {HistoryTableStore} from "./HistoryTableStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {PlatformPreferences} from "../models/PlatformPreferences.ts";
import Cookies from "js-cookie";

export class RootStore {
  preferencesStore: PreferencesStore;
  gatewayTableStore: GatewayTableStore;
  gatewayEditStore: GatewayEditStore;
  gatewayAddStore: GatewayAddStore;
  gatewayFilterStore: GatewayFilterStore;
  scheduleTableStore: ScheduleTableStore;
  scheduleEditStore: ScheduleEditStore;
  generateScheduleStore: GenerateScheduleStore;
  createScheduleStore: CreateScheduleStore;
  bulkActionStore: BulkActionStore;
  historyTableStore: HistoryTableStore;

  platformPreferences: PlatformPreferences = {
    navigationBarOpen: false,
    colorTheme: "dark"
  }

  constructor() {
    this.preferencesStore = new PreferencesStore(this);
    this.gatewayTableStore = new GatewayTableStore(this);
    this.gatewayEditStore = new GatewayEditStore(this);
    this.gatewayAddStore = new GatewayAddStore(this);
    this.scheduleTableStore = new ScheduleTableStore(this);
    this.gatewayFilterStore = new GatewayFilterStore(this);
    this.scheduleEditStore = new ScheduleEditStore(this);
    this.generateScheduleStore = new GenerateScheduleStore(this);
    this.createScheduleStore = new CreateScheduleStore(this);
    this.bulkActionStore = new BulkActionStore(this);
    this.historyTableStore = new HistoryTableStore(this);
    makeAutoObservable(this);
  }

  setNavigationBarOpen = (open: boolean) => {
    action(() => {
      this.platformPreferences.navigationBarOpen = open;
      Cookies.set("cs-platform-preferences", JSON.stringify(this.platformPreferences));
    })();
  }

  setColorTheme = (theme: "dark" | "light") => {
    action(() => {
      this.platformPreferences.colorTheme = theme;
      Cookies.set("cs-platform-preferences", JSON.stringify(this.platformPreferences));
    })();
  }

  setPlatformPreferences = (preferences: PlatformPreferences) => {
    action(() => {
      this.platformPreferences = preferences;
    })();
  }
}
