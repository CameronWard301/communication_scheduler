export interface RetryPolicy {
  maximumAttempts: number,
  backoffCoefficient: number
  initialInterval: String
  maximumInterval: String
  startToCloseTimeout: String
}

export interface GatewayTimeout {
  gatewayTimeoutSeconds: number,

}

export interface ServerPreferences {
  gatewayTimeoutSeconds: GatewayTimeout;
  retryPolicy: RetryPolicy;
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
