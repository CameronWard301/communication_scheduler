import {useStore} from "../context/StoreContext.tsx";
import {useAxiosClientContext} from "../context/AxiosContext.tsx";
import {useToken} from "./AuthenticationService.ts";
import {useErrorHandling} from "../helper/UseErrorHandling.ts";
import {ConfigContext} from "../context/ConfigContext.tsx";
import {useContext} from "react";
import {CreateScheduleRequest, Schedule, SchedulePage} from "../models/Schedules.ts";
import {SnackbarContext} from "../context/SnackbarContext.tsx";
import {useNavigate} from "react-router-dom";

export const useScheduleService = () => {
  const rootStore = useStore();
  const client = useAxiosClientContext();
  const authToken = useToken();
  const {handleError} = useErrorHandling();
  const [config] = useContext(ConfigContext);
  const snackbar = useContext(SnackbarContext);
  const navigate = useNavigate();

  const getScheduleTable = () => {
    rootStore.scheduleTableStore.setLoading(true);
    const params = new URLSearchParams({
      pageSize: rootStore.scheduleTableStore.paginationModel.pageSize.toString(),
      pageNumber: rootStore.scheduleTableStore.paginationModel.page.toString(),
    });
    if (rootStore.scheduleTableStore.scheduleIdFilter !== "") {
      params.append("scheduleId", rootStore.scheduleTableStore.scheduleIdFilter);
    }
    if (rootStore.scheduleTableStore.gatewayIdFilter !== "") {
      params.append("gatewayId", rootStore.scheduleTableStore.gatewayIdFilter);
    }
    if (rootStore.scheduleTableStore.userIdFilter !== "") {
      params.append("userId", rootStore.scheduleTableStore.userIdFilter);
    }
    if (rootStore.scheduleTableStore.sortModel.length > 0) {
      rootStore.scheduleTableStore.sortModel.forEach((sortModel) => {
        params.append("sort", `${sortModel.field}`);
        params.append("sortDirection", `${sortModel.sort as string}`);
      })
    }

    authToken.then((token) => {
      client.get(`${config.bffBaseUrl}/schedule`, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
        params: params
      }).then(response => {
        const schedulePage = response.data as SchedulePage;
        rootStore.scheduleTableStore.setScheduleTableData(schedulePage.schedules);
        rootStore.scheduleTableStore.setTotalCount(schedulePage.totalElements);
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => rootStore.scheduleTableStore.setLoading(false));
    }).catch(error => {
        handleError(error)
        rootStore.scheduleTableStore.setLoading(false)
      })

  }

  const getScheduleById = (id: string) => {
    rootStore.scheduleTableStore.setLoading(true);
    rootStore.scheduleEditStore.setLoading(true);
    authToken.then((token) => {
      client.get(`${config.bffBaseUrl}/schedule/${id}`, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then(response => {
        const schedule = response.data as Schedule;
        rootStore.scheduleEditStore.setServerSchedule(schedule);
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => {
          rootStore.scheduleEditStore.setLoading(false)
          rootStore.scheduleTableStore.setLoading(false)
        });
    }).catch(error => {
        handleError(error)
        rootStore.scheduleEditStore.setLoading(false)
        rootStore.scheduleTableStore.setLoading(false)
      })
  }

  const pauseSchedule = (id: string) => {
    rootStore.scheduleTableStore.setLoading(true);
    rootStore.scheduleEditStore.setLoading(true);
    authToken.then((token) => {
      client.patch(`${config.bffBaseUrl}/schedule/${id}/pause`, {}, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((result) => {
        snackbar.addSnackbar("Schedule paused", "success");
        rootStore.scheduleTableStore.updateScheduleById(result.data as Schedule)
        rootStore.scheduleTableStore.setLoading(false)
        rootStore.scheduleTableStore.setConfirmPauseModalOpen(false);
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => {
          rootStore.scheduleEditStore.setLoading(false)
        });
    }).catch(error => {
      handleError(error)
      rootStore.scheduleTableStore.setLoading(false)
      rootStore.scheduleEditStore.setLoading(false)
    })
  }
  const resumeSchedule = (id: string) => {
    rootStore.scheduleTableStore.setLoading(true);
    rootStore.scheduleEditStore.setLoading(true);
    authToken.then((token) => {
      client.patch(`${config.bffBaseUrl}/schedule/${id}/resume`, {}, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((result) => {
        snackbar.addSnackbar("Schedule Running", "success");
        rootStore.scheduleTableStore.updateScheduleById(result.data as Schedule)
        rootStore.scheduleTableStore.setLoading(false)
        rootStore.scheduleTableStore.setConfirmResumeModalOpen(false);
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => {
          rootStore.scheduleEditStore.setLoading(false)
        });
    }).catch(error => {
      handleError(error)
      rootStore.scheduleTableStore.setLoading(false)
      rootStore.scheduleEditStore.setLoading(false)
    })
  }

  const createSchedule = (createScheduleRequest: CreateScheduleRequest) => {
    rootStore.scheduleAddStore.setLoading(true);
    authToken.then((token) => {
      client.post(`${config.bffBaseUrl}/schedule`, createScheduleRequest, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((response) => {
        snackbar.addSnackbar("Schedule created", "success");
        getScheduleTable();
        navigate("/schedules/?scheduleId=" + (response.data as Schedule).id)
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => rootStore.scheduleAddStore.setLoading(false));
    }).catch(error => {
      handleError(error)
      rootStore.scheduleAddStore.setLoading(false)
    })
  }

  return {getScheduleTable, pauseSchedule, getScheduleById, resumeSchedule, createSchedule}
}
