import {RootStore} from "./RootStore.tsx";
import {action, makeAutoObservable} from "mobx";
import {Preferences} from "../models/Preferences.ts";

export class PreferencesStore {
  rootStore: RootStore;
  modalOpen = false;
  isLoading = false;

  isAdvancedMode = false;
  newMaximumAttempts = 0;
  newBackoffCoefficient = 1;
  newInitialInterval = 0;
  newMaximumInterval = 0;
  newGatewayTimeout = 0;
  newStartToCloseTimeout = 0;
  newGatewayTimeoutTime = "S";
  newInitialIntervalTime = "S"
  newMaximumIntervalTime = "S"
  newStartToCloseTimeoutTime = "S"

  currentMaximumAttempts = 0;
  currentBackoffCoefficient = 1;
  currentInitialInterval = 0;
  currentMaximumInterval = 0;
  currentGatewayTimeout = 0;
  currentStartToCloseTimeout = 0;
  currentGatewayTimeoutTime = "S";
  currentInitialIntervalTime = "S"
  currentMaximumIntervalTime = "S"
  currentStartToCloseTimeoutTime = "S"


  constructor(rootStore: RootStore) {
    this.rootStore = rootStore;
    makeAutoObservable(this);
  }

  setModalOpen = (open: boolean) => {
    action(() => {
      this.modalOpen = open;
    })();
  }

  setLoading = (loading: boolean) => {
    action(() => {
      this.isLoading = loading;
    })();
  }

  setFromServer = (preferences: Preferences) => {
    action(() => {
      this.newMaximumAttempts = preferences.maximumAttempts;
      this.newBackoffCoefficient = preferences.backoffCoefficient;
      this.newInitialInterval = preferences.initialInterval.value;
      this.newMaximumInterval = preferences.maximumInterval.value;
      this.newGatewayTimeout = preferences.gatewayTimeout.value;
      this.newStartToCloseTimeout = preferences.startToCloseTimeout.value;

      this.newGatewayTimeoutTime = preferences.gatewayTimeout.unit;
      this.newInitialIntervalTime = preferences.initialInterval.unit;
      this.newMaximumIntervalTime = preferences.initialInterval.unit;
      this.newStartToCloseTimeoutTime = preferences.startToCloseTimeout.unit;

      this.currentMaximumAttempts = preferences.maximumAttempts;
      this.currentBackoffCoefficient = preferences.backoffCoefficient;
      this.currentInitialInterval = preferences.initialInterval.value;
      this.currentMaximumInterval = preferences.maximumInterval.value;
      this.currentGatewayTimeout = preferences.gatewayTimeout.value;
      this.currentStartToCloseTimeout = preferences.startToCloseTimeout.value;

      this.currentGatewayTimeoutTime = preferences.gatewayTimeout.unit;
      this.currentInitialIntervalTime = preferences.initialInterval.unit;
      this.currentMaximumIntervalTime = preferences.initialInterval.unit;
      this.currentStartToCloseTimeoutTime = preferences.startToCloseTimeout.unit;
    })();
  }

  getPreferencesUpdate = (): Preferences => {
    return {
      maximumAttempts: this.newMaximumAttempts,
      backoffCoefficient: this.newBackoffCoefficient,
      gatewayTimeout: {
        value: this.newGatewayTimeout,
        unit: this.newGatewayTimeoutTime
      },
      initialInterval: {
        value: this.newInitialInterval,
        unit: this.newInitialIntervalTime
      },
      maximumInterval: {
        value: this.newMaximumInterval,
        unit: this.newMaximumIntervalTime
      },
      startToCloseTimeout: {
        value: this.newStartToCloseTimeout,
        unit: this.newStartToCloseTimeoutTime
      }
    }
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

  setGatewayTimeout = (timeout: string) => {
    action(() => {
      this.newGatewayTimeout = Number(timeout);
    })();
  }

  setGatewayTimeoutTime = (time: string) => {
    action(() => {
      this.newGatewayTimeoutTime = time;
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

  hasMaximumAttemptsChanged = () => {
    return this.newMaximumAttempts !== this.currentMaximumAttempts;
  }

  hasBackoffCoefficientChanged = () => {
    return this.newBackoffCoefficient !== this.currentBackoffCoefficient;
  }

  hasInitialIntervalChanged = () => {
    return this.newInitialInterval !== this.currentInitialInterval || this.newInitialIntervalTime !== this.currentInitialIntervalTime;
  }

  hasMaximumIntervalChanged = () => {
    return this.newMaximumInterval !== this.currentMaximumInterval || this.newMaximumIntervalTime !== this.currentMaximumIntervalTime;
  }

  hasGatewayTimeoutChanged = () => {
    return this.newGatewayTimeout !== this.currentGatewayTimeout || this.newGatewayTimeoutTime !== this.currentGatewayTimeoutTime;
  }

  hasStartToCloseTimeoutChanged = () => {
    return this.newStartToCloseTimeout !== this.currentStartToCloseTimeout || this.newStartToCloseTimeoutTime !== this.currentStartToCloseTimeoutTime;
  }

  hasChanged = () => {
    return this.hasMaximumAttemptsChanged() || this.hasBackoffCoefficientChanged() || this.hasInitialIntervalChanged() || this.hasMaximumIntervalChanged() || this.hasGatewayTimeoutChanged() || this.hasStartToCloseTimeoutChanged();
  }

}
