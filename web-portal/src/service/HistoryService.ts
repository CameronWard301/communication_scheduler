import {useStore} from "../context/StoreContext.tsx";
import {HistoryPage} from "../models/History.ts";
import {useToken} from "./AuthenticationService.ts";
import {useAxiosClientContext} from "../context/AxiosContext.tsx";
import {useContext} from "react";
import {ConfigContext} from "../context/ConfigContext.tsx";
import {useErrorHandling} from "../helper/UseErrorHandling.ts";
import {SnackbarContext} from "../context/SnackbarContext.tsx";
import {AxiosError} from "axios";

export const useHistoryService = () => {
  const rootStore = useStore()
  const authToken = useToken();
  const client = useAxiosClientContext();
  const [config] = useContext(ConfigContext);
  const {handleError} = useErrorHandling();
  const snackbar = useContext(SnackbarContext);

  const getHistoryFilterParams = () => {
    const params = new URLSearchParams({
      pageSize: rootStore.historyTableStore.paginationModel.pageSize.toString(),
      pageNumber: rootStore.historyTableStore.paginationModel.page.toString(),
    });
    if (rootStore.historyTableStore.statusFilter.length > 0 && rootStore.historyTableStore.statusFilter[0] !== "Any Status") {
      params.append("status", rootStore.historyTableStore.statusFilter.toString());
    }

    if (rootStore.historyTableStore.gatewayIdFilter !== "") {
      params.append("gatewayId", rootStore.historyTableStore.gatewayIdFilter);
    }

    if (rootStore.historyTableStore.scheduleIdFilter !== "") {
      params.append("scheduleId", rootStore.historyTableStore.scheduleIdFilter);
    }

    if (rootStore.historyTableStore.userIdFilter !== "") {
      params.append("userId", rootStore.historyTableStore.userIdFilter);
    }
    return params;
  }

  const getHistoryPage = () => {
    rootStore.historyTableStore.setLoading(true);
    const params = getHistoryFilterParams();

    authToken.then((token) => {
      client.get(config.bffBaseUrl + "/history", {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
        params: params
      }).then(response => {
        const historyPage = response.data as HistoryPage;
        rootStore.historyTableStore.setHistoryTableData(historyPage.historyItems);
        rootStore.historyTableStore.setTotalCount(historyPage.totalElements);
      }).catch((error) => {
        handleError(error);
      }).finally(() => {
        rootStore.historyTableStore.setLoading(false);
      });
    }).catch((error) => {
      handleError(error);
      rootStore.historyTableStore.setLoading(false);
    })
  }

  const stopCommunication = () => {
    if (!rootStore.historyTableStore.selectedHistoryItem) {
      return;
    }
    rootStore.historyTableStore.setLoading(true);
    authToken.then((token) => {
      client.delete(`${config.bffBaseUrl}/history/stop/${rootStore.historyTableStore.selectedHistoryItem!.workflowId}/${rootStore.historyTableStore.selectedHistoryItem!.id}"`, {
        headers: {
          "Authorization": "Bearer " + token.token,
        }
      }).then(() => {
        snackbar.addSnackbar("Communication Stopped", "success");
        rootStore.historyTableStore.updateHistoryItemStatus(rootStore.historyTableStore.selectedHistoryItem!.id)
        rootStore.historyTableStore.setOpenConfirmStopModal(false);
      }).catch((error) => {
        if (error instanceof AxiosError && error.response) {
          if (error.response.status === 404) {
            snackbar.addSnackbar("Could not stop communication, maybe it has already been completed", "warning");
            return
          }
        }

        handleError(error);
      }).finally(() => {
        rootStore.historyTableStore.setLoading(false);
      });
    }).catch((error) => {
      handleError(error);
      rootStore.historyTableStore.setLoading(false);
    })
  }

  return {getHistoryPage, stopCommunication}
}
