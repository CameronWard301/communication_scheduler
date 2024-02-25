import {ClientPreferences, ServerPreferences, TimeUnit} from "../model/Preferences";
import axiosClient from "../../../axios-client";
import {BFFResponse} from "../../../model/BFFResponse";
import extractAuthToken from "../../../helper/extract-auth-token";

const getTimeUnit = (value: String): TimeUnit => {
  //Values from the server are in the format PT1S, PT1M, PT1H, PT100D
  value = value.replace("PT", "");

  //slice the string on the last character to get the unit
  const unit = value.slice(-1);
  return {
    value: parseInt(value.slice(0, -1)),
    unit: unit
  }
}

export const PreferencesService = () => {

  const getPreferences = async (token:string | undefined): Promise<BFFResponse<ClientPreferences>> => {
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
          startToCloseTimeout: getTimeUnit(serverPreferences.retryPolicy.startToCloseTimeout),
        } as ClientPreferences
      };

    }).catch((reason) => {
      throw reason;
    });
  }

  return {getPreferences};
}
