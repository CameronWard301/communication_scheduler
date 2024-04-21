import { ClientPreferences, GatewayTimeout, ServerPreferences, TimeUnit } from "../model/Preferences";
import axiosClient from "../../../axios-client";
import { BFFResponse } from "../../../model/BFFResponse";
import extractAuthToken from "../../../helper/extract-auth-token";

const getTimeUnit = (value: String): TimeUnit => {
  //Values from the server are in the format PT1S, PT1M, PT1H, P10D
  value = value.replace("PT", "");
  value = value.replace("P", "");

  //slice the string on the last character to get the unit
  const unit = value.slice(-1);
  return {
    value: parseInt(value.slice(0, -1)),
    unit: unit
  };
};

const getDurationString = (timeUnit: TimeUnit): string => {
  //Values to the server are in the format PT1S, PT1M, PT1H, P10D
  return "P" + (timeUnit.unit.toUpperCase() === "D" ? "" : "T") + timeUnit.value + timeUnit.unit.toUpperCase();
};

const convertToSeconds = (timeUnit: TimeUnit): number => {
  timeUnit.unit = timeUnit.unit.toUpperCase();
  switch (timeUnit.unit) {
    case "S":
      return timeUnit.value;
    case "M":
      return timeUnit.value * 60;
    case "H":
      return timeUnit.value * 3600;
    case "D":
      return timeUnit.value * 86400;
    case "Y":
      return timeUnit.value * 31536000;
    default:
      throw new Error("Invalid time unit");
  }
};

export const PreferencesService = () => {

  const getPreferences = async (token: string | undefined): Promise<BFFResponse<ClientPreferences>> => {
    return await axiosClient.get(process.env.PREFERENCES_API_URL as string, {
      headers: extractAuthToken(token)

    }).then((value) => {
      const serverPreferences = value.data as ServerPreferences;
      return {
        status: value.status,
        data: {
          maximumAttempts: serverPreferences.retryPolicy.maximumAttempts,
          backoffCoefficient: serverPreferences.retryPolicy.backoffCoefficient,
          gatewayTimeout: getTimeUnit(serverPreferences.gatewayTimeoutSeconds + "S"),
          initialInterval: getTimeUnit(serverPreferences.retryPolicy.initialInterval),
          maximumInterval: getTimeUnit(serverPreferences.retryPolicy.maximumInterval),
          startToCloseTimeout: getTimeUnit(serverPreferences.retryPolicy.startToCloseTimeout)
        } as ClientPreferences
      };

    }).catch((reason) => {
      throw reason;
    });
  };

  const putPreferences = async (token: string | undefined, preferences: ClientPreferences): Promise<BFFResponse<ClientPreferences>> => {
    const gatewayTimeout = await axiosClient.put(process.env.PREFERENCES_API_URL as string + "/gateway-timeout", {
      gatewayTimeoutSeconds: convertToSeconds(preferences.gatewayTimeout)
    }, {
      headers: extractAuthToken(token)
    });

    const retryPolicy = await axiosClient.put(process.env.PREFERENCES_API_URL as string + "/retry-policy", {
      maximumAttempts: preferences.maximumAttempts,
      backoffCoefficient: preferences.backoffCoefficient,
      initialInterval: getDurationString(preferences.initialInterval),
      maximumInterval: getDurationString(preferences.maximumInterval),
      startToCloseTimeout: getDurationString(preferences.startToCloseTimeout)
    }, {
      headers: extractAuthToken(token)
    });

    return {
      status: 200,
      data: {
        maximumAttempts: retryPolicy.data.maximumAttempts,
        backoffCoefficient: retryPolicy.data.backoffCoefficient,
        gatewayTimeout: getTimeUnit((gatewayTimeout.data as GatewayTimeout).gatewayTimeoutSeconds + "S"),
        initialInterval: getTimeUnit(retryPolicy.data.initialInterval),
        maximumInterval: getTimeUnit(retryPolicy.data.maximumInterval),
        startToCloseTimeout: getTimeUnit(retryPolicy.data.startToCloseTimeout)
      } as ClientPreferences
    };

  };

  return { getPreferences, putPreferences };
};
