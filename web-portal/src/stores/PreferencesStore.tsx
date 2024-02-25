import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";

export class PreferencesStore {
  rootStore: RootStore;
  isAdvancedMode = false;
  newMaximumAttempts = 0;
  newBackoffCoefficient = 1;
  newInitialInterval = 0;
  newMaximumInterval = 0;
  newStartToCloseTimeout = 0;
  newInitialIntervalTime = "S"
  newMaximumIntervalTime = "S"
  newStartToCloseTimeoutTime = "S"
  currentMaximumAttempts = 0;

  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this);
  }

  setAdvancedMode = (mode: boolean) => {
    action(() => {
      this.isAdvancedMode = mode;
    })();
  }

  setMaximumAttempts = (attempts: string) => {
    action(() => {
      this.newMaximumAttempts = Number(attempts);
    })();
  }

  setBackoffCoefficient = (coefficient: string) => {
    action(() => {
      this.newBackoffCoefficient = Number(coefficient);
    })();
  }

  setInitialInterval = (interval: string) => {
    action(() => {
      this.newInitialInterval = Number(interval);
    })();
  }

  setMaximumInterval = (interval: string) => {
    action(() => {
      this.newMaximumInterval = Number(interval);
    })();
  }
  setStartToCloseTimeout = (timeout: string) => {
    action(() => {
      this.newStartToCloseTimeout = Number(timeout);
    })();
  }

  setMaximumIntervalTime = (time: string) => {
    action(() => {
      this.newMaximumIntervalTime = time;
    })();
  }

  setInitialIntervalTime = (time: string) => {
    action(() => {
      this.newInitialIntervalTime = time;
    })();
  }

  setStartToCloseTimeoutTime = (time: string) => {
    action(() => {
      this.newStartToCloseTimeoutTime = time;
    })();
  }

}
