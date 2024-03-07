import {PreferencesStore} from "./PreferencesStore.tsx";
import {GatewayTableStore} from "./GatewayTableStore.tsx";
import {GatewayEditStore} from "./GatewayEditStore.tsx";
import {GatewayAddStore} from "./GatewayAddStore.tsx";

export class RootStore {
  preferencesStore: PreferencesStore;
  gatewayTableStore: GatewayTableStore;
  gatewayEditStore: GatewayEditStore;
  gatewayAddStore: GatewayAddStore;

  constructor() {
    this.preferencesStore = new PreferencesStore(this);
    this.gatewayTableStore = new GatewayTableStore(this);
    this.gatewayEditStore = new GatewayEditStore(this);
    this.gatewayAddStore = new GatewayAddStore(this);
  }
}
