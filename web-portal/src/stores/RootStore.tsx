import {PreferencesStore} from "./PreferencesStore.tsx";
import {GatewayTableStore} from "./GatewayTableStore.tsx";

export class RootStore {
  preferencesStore: PreferencesStore;
  gatewayTableStore: GatewayTableStore;

  constructor() {
    this.preferencesStore = new PreferencesStore(this);
    this.gatewayTableStore = new GatewayTableStore(this);
  }
}
