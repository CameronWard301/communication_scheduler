export interface ServerPreferences {
  gatewayTimeoutSeconds: number,
  retryPolicy: {
    maximumAttempts: number,
    backoffCoefficient: number
    initialInterval: String
    maximumInterval: String
    startToCloseTimeout: String
  }
}

export interface TimeUnit {
  value: number;
  unit: string;
}

export interface ClientPreferences {
  maximumAttempts: number;
  backoffCoefficient: number;
  gatewayTimeout: TimeUnit,
  initialInterval: TimeUnit
  maximumInterval: TimeUnit
  startToCloseTimeout: TimeUnit
}
