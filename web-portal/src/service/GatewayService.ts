import {useAxiosClientContext} from "../context/AxiosContext.tsx";
import {useToken} from "./AuthenticationService.ts";
import {useStore} from "../context/StoreContext.tsx";
import {useContext} from "react";
import {ConfigContext} from "../context/ConfigContext.tsx";
import {useErrorHandling} from "../helper/UseErrorHandling.ts";
import {Gateway, GatewayPage, TotalMatches} from "../models/Gateways.ts";
import {SnackbarContext} from "../context/SnackbarContext.tsx";
import {useNavigate} from "react-router-dom";

export const useGatewayService = () => {
  const client = useAxiosClientContext();
  const authToken = useToken();
  const rootStore = useStore();
  const [config] = useContext(ConfigContext);
  const {handleError} = useErrorHandling();
  const snackbar = useContext(SnackbarContext);
  const navigate = useNavigate();
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
    if (rootStore.gatewayTableStore.gatewayIdFilter !== "") {
      params.append("gatewayId", rootStore.gatewayTableStore.gatewayIdFilter);
    }
    if (rootStore.gatewayTableStore.gatewayNameFilter !== "") {
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

  const getAffectedSchedules = (gatewayId: string) => {
    if (gatewayId === "") {
      return;
    }
    rootStore.gatewayTableStore.setLoading(true);
    authToken.then((token) => {
      client.get(`${config.bffBaseUrl}/gateway/${gatewayId}/schedule/count`, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then(response => {
        const gatewayPage = response.data as TotalMatches;
        rootStore.gatewayTableStore.setAffectedSchedules(gatewayPage.total);
        rootStore.gatewayEditStore.setAffectedSchedules(gatewayPage.total);
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

  const getGatewayById = (gatewayId: string) => {
    rootStore.gatewayEditStore.setIsLoading(true);
    authToken.then((token) => {
      client.get(`${config.bffBaseUrl}/gateway/${gatewayId}`, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((response) => {
        rootStore.gatewayEditStore.setGateway(response.data as Gateway)
        rootStore.gatewayEditStore.setUpdatedGateway(response.data as Gateway)
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => rootStore.gatewayEditStore.setIsLoading(false));
    }).catch(error => {
      handleError(error)
      rootStore.gatewayEditStore.setIsLoading(false)
    })
  }

  const createGateway = (gateway: Gateway) => {
    rootStore.gatewayAddStore.setLoading(true);
    authToken.then((token) => {
      client.post(`${config.bffBaseUrl}/gateway`, gateway, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((response) => {
        navigate("/gateways/?gatewayId=" + (response.data as Gateway).id)
        snackbar.addSnackbar("Gateway added", "success");
        rootStore.gatewayAddStore.setConfirmModalOpen(false)
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => rootStore.gatewayAddStore.setLoading(false));
    }).catch(error => {
      handleError(error)
      rootStore.gatewayAddStore.setLoading(false)
    })

  }

  const updateGateway = (gateway: Gateway) => {
    rootStore.gatewayEditStore.setIsLoading(true);
    authToken.then((token) => {
      client.put(`${config.bffBaseUrl}/gateway`, gateway, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((response) => {
        rootStore.gatewayEditStore.setGateway(response.data as Gateway)
        rootStore.gatewayEditStore.setConfirmModalOpen(false)
        snackbar.addSnackbar("Gateway updated", "success");
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => rootStore.gatewayEditStore.setIsLoading(false));
    }).catch(error => {
      handleError(error)
      rootStore.gatewayEditStore.setIsLoading(false)
    })
  }

  const deleteGatewayById = (gateway: Gateway) => {
    rootStore.gatewayTableStore.setLoading(true);
    authToken.then((token) => {
      client.delete(`${config.bffBaseUrl}/gateway/${gateway.id}`, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then(() => {
        snackbar.addSnackbar("Gateway deleted", "success");
        rootStore.gatewayTableStore.setDeleteModalOpen(false);
        rootStore.gatewayEditStore.setDeleteModalOpen(false);
        getGateways();
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => {
          rootStore.gatewayTableStore.setLoading(false)
          rootStore.gatewayEditStore.setIsLoading(false)
        });
    }).catch(error => {
      handleError(error)
      rootStore.gatewayTableStore.setLoading(false)
      rootStore.gatewayEditStore.setIsLoading(false)


    })
  }

  return {getGateways, getAffectedSchedules, deleteGatewayById, getGatewayById, updateGateway, createGateway}
}
