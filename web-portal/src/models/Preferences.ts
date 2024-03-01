interface TimeUnit {
  value: number;
  unit: string;
}

export interface Preferences {
  maximumAttempts: number;
  backoffCoefficient: number;
  gatewayTimeout: TimeUnit;
  initialInterval: TimeUnit
  maximumInterval: TimeUnit
  startToCloseTimeout: TimeUnit
}
