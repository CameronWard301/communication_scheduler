import {useAxiosClientContext} from "../context/AxiosContext.tsx";
import {useToken} from "./AuthenticationService.ts";
import {useStore} from "../context/StoreContext.tsx";
import {useContext} from "react";
import {ConfigContext} from "../context/ConfigContext.tsx";
import {useErrorHandling} from "../helper/UseErrorHandling.ts";
import {GatewayPage} from "../models/Gateways.ts";

export const useGatewayService = () => {
  const client = useAxiosClientContext();
  const authToken = useToken();
  const rootStore = useStore();
  const [config] = useContext(ConfigContext);
  const {handleError} = useErrorHandling();
  const getGateways = () => {
    rootStore.gatewayTableStore.setLoading(true);
    const params = new URLSearchParams({
      pageSize: rootStore.gatewayTableStore.paginationModel.pageSize.toString(),
      pageNumber: rootStore.gatewayTableStore.paginationModel.page.toString(),
    });
    if (rootStore.gatewayTableStore.sortModel.length > 0) {
      rootStore.gatewayTableStore.sortModel.forEach((sortModel) => {
        params.append("sort", `${sortModel.field}`);
        params.append("sortDirection", `${sortModel.sort as string}`);
      })
    }
    if (rootStore.gatewayTableStore.gatewayIdFilter !== ""){
      params.append("gatewayId", rootStore.gatewayTableStore.gatewayIdFilter);
    }
    if (rootStore.gatewayTableStore.gatewayNameFilter !== ""){
      params.append("friendlyName", rootStore.gatewayTableStore.gatewayNameFilter);
    }
    if (rootStore.gatewayTableStore.gatewayDescriptionFilter !== "") {
      params.append("description", rootStore.gatewayTableStore.gatewayDescriptionFilter);
    }
    if (rootStore.gatewayTableStore.gatewayEndpointUrlFilter !== "") {
      params.append("endpointUrl", rootStore.gatewayTableStore.gatewayEndpointUrlFilter);
    }
    authToken.then((token) => {
      client.get(config.bffBaseUrl + "/gateway", {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
        params: params
      }).then(response => {
        const gatewayPage = response.data as GatewayPage;
        rootStore.gatewayTableStore.setGatewayTableData(gatewayPage.gateways);
        rootStore.gatewayTableStore.setTotalCount(gatewayPage.totalElements);
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => rootStore.gatewayTableStore.setLoading(false));
    }).catch(error => {
      handleError(error)
      rootStore.gatewayTableStore.setLoading(false)
    })
  }

  return {getGateways}
}
