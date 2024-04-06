import { AuthToken } from "../model/auth-models";
import axiosClient from "../../../axios-client";
import { BFFResponse } from "../../../model/BFFResponse";


export const AuthService = () => {
  const getAuthToken = async (scopes: string[]): Promise<BFFResponse<AuthToken>> => {
    return await axiosClient.post(process.env.AUTH_API_URL + "/auth", scopes).then((value) => {
      return { status: value.status, data: value.data as AuthToken };
    }).catch((reason) => {
      throw reason;
    });
  };
  return { getAuthToken };
};
