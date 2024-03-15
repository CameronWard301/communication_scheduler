import {PreferencesStore} from "./PreferencesStore.tsx";
import {GatewayTableStore} from "./GatewayTableStore.tsx";
import {GatewayEditStore} from "./GatewayEditStore.tsx";
import {GatewayAddStore} from "./GatewayAddStore.tsx";
import {ScheduleTableStore} from "./ScheduleTableStore.tsx";
import {GatewayFilterStore} from "./GatewayFilterStore.tsx";
import { ScheduleEditStore } from "./ScheduleEditStore.tsx";
import {ScheduleAddStore} from "./ScheduleAddStore.tsx";
import {CreateScheduleStore} from "./CreateScheduleStore.tsx";

export class RootStore {
  preferencesStore: PreferencesStore;
  gatewayTableStore: GatewayTableStore;
  gatewayEditStore: GatewayEditStore;
  gatewayAddStore: GatewayAddStore;
  gatewayFilterStore: GatewayFilterStore;
  scheduleTableStore: ScheduleTableStore;
  scheduleEditStore: ScheduleEditStore;
  scheduleAddStore: ScheduleAddStore;
  createScheduleStore: CreateScheduleStore;

  constructor() {
    this.preferencesStore = new PreferencesStore(this);
    this.gatewayTableStore = new GatewayTableStore(this);
    this.gatewayEditStore = new GatewayEditStore(this);
    this.gatewayAddStore = new GatewayAddStore(this);
    this.scheduleTableStore = new ScheduleTableStore(this);
    this.gatewayFilterStore = new GatewayFilterStore(this);
    this.scheduleEditStore = new ScheduleEditStore(this);
    this.scheduleAddStore = new ScheduleAddStore(this);
    this.createScheduleStore = new CreateScheduleStore(this);
  }
}
