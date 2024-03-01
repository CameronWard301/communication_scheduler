import {PreferencesStore} from "./PreferencesStore.tsx";

export class RootStore {
  preferencesStore: PreferencesStore;

  constructor() {
    this.preferencesStore = new PreferencesStore(this);
  }
}
