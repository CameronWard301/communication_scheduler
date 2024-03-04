import {PreferencesStore} from "./PreferencesStore.tsx";
import {GatewayTableStore} from "./GatewayTableStore.tsx";
import {GatewayEditStore} from "./GatewayEditStore.tsx";

export class RootStore {
  preferencesStore: PreferencesStore;
  gatewayTableStore: GatewayTableStore;
  gatewayEditStore: GatewayEditStore;

  constructor() {
    this.preferencesStore = new PreferencesStore(this);
    this.gatewayTableStore = new GatewayTableStore(this);
    this.gatewayEditStore = new GatewayEditStore(this);
  }
}
