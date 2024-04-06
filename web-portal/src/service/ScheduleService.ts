import {useStore} from "../context/StoreContext.tsx";
import {useAxiosClientContext} from "../context/AxiosContext.tsx";
import {useToken} from "./AuthenticationService.ts";
import {useErrorHandling} from "../helper/UseErrorHandling.ts";
import {ConfigContext} from "../context/ConfigContext.tsx";
import {useContext} from "react";
import {
  BulkActionSelectionType,
  BulkUpdateResponse,
  BulkUpdateScheduleRequest,
  CreateScheduleRequest,
  Schedule,
  SchedulePage,
  ScheduleStatus,
  UpdateScheduleRequest
} from "../models/Schedules.ts";
import {SnackbarContext} from "../context/SnackbarContext.tsx";
import {useNavigate} from "react-router-dom";
import {AxiosError} from "axios";
import {ScheduleTableStore} from "../stores/ScheduleTableStore.tsx";
import {Gateway} from "../models/Gateways.ts";

export const useScheduleService = () => {
  const rootStore = useStore();
  const client = useAxiosClientContext();
  const authToken = useToken();
  const {handleError} = useErrorHandling();
  const [config] = useContext(ConfigContext);
  const snackbar = useContext(SnackbarContext);
  const navigate = useNavigate();

  const createParams = (store: ScheduleTableStore, selectById: boolean) => {
    const params = new URLSearchParams({
      pageSize: store.paginationModel.pageSize.toString(),
      pageNumber: store.paginationModel.page.toString(),
    });
    if (store.scheduleIdFilter !== "") {
      params.append("scheduleId", store.scheduleIdFilter);
    }
    if (store.gatewayIdFilter !== "") {
      params.append("gatewayId", store.gatewayIdFilter);
    }
    if (store.userIdFilter !== "") {
      params.append("userId", store.userIdFilter);
    }

    if (selectById || store.scheduleIdFilter !== "") {
      params.append("selectionType", BulkActionSelectionType.IDs);
      const selectedIds = store.checkBoxSelectionModel.map((id) => id as string);
      params.append("scheduleIds", JSON.stringify(selectedIds));

    } else {
      params.append("selectionType", BulkActionSelectionType.QUERY);
    }

    return params;
  };

  const fetchTableData = (url: string, selectById = false) => {
    const {scheduleTableStore} = rootStore;
    scheduleTableStore.setLoading(true);
    const params = createParams(scheduleTableStore, selectById);

    authToken.then((token) => {
      client.get(`${config.bffBaseUrl}${url}`, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
        params: params
      }).then(response => {
        const schedulePage = response.data as SchedulePage;
        scheduleTableStore.setScheduleTableData(schedulePage.schedules);
        scheduleTableStore.setTotalCount(schedulePage.totalElements);
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => scheduleTableStore.setLoading(false));
    }).catch(error => {
      handleError(error)
      scheduleTableStore.setLoading(false)
    })
  };

  const getScheduleReviewTable = () => {
    fetchTableData("/schedule/review", !rootStore.scheduleTableStore.selectedAll);
  }

  const getScheduleTable = () => {
    fetchTableData("/schedule")
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
          if (error instanceof AxiosError) {
            if (error.response?.status === 404) {
              snackbar.addSnackbar("Schedule not found with id: " + id, "error");
              return;
            }
          }
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

  const pauseSchedule = (id: string, gatewayName: string) => {
    rootStore.scheduleTableStore.setLoading(true);
    rootStore.scheduleEditStore.setLoading(true);
    authToken.then((token) => {
      client.patch(`${config.bffBaseUrl}/schedule/${id}/pause`, {
        friendlyName: gatewayName
      } as Gateway, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((result) => {
        snackbar.addSnackbar("Schedule Paused", "success");
        rootStore.scheduleTableStore.updateScheduleById(result.data as Schedule)
        rootStore.scheduleTableStore.setConfirmPauseModalOpen(false);
        rootStore.scheduleEditStore.setStatus(ScheduleStatus.Paused)
        rootStore.scheduleEditStore.setConfirmPauseModalOpen(false);
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
      rootStore.scheduleTableStore.setLoading(false)
      rootStore.scheduleEditStore.setLoading(false)
    })
  }
  const resumeSchedule = (id: string, gatewayName: string) => {
    rootStore.scheduleTableStore.setLoading(true);
    rootStore.scheduleEditStore.setLoading(true);
    authToken.then((token) => {
      client.patch(`${config.bffBaseUrl}/schedule/${id}/resume`, {
        friendlyName: gatewayName
      } as Gateway, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((result) => {
        snackbar.addSnackbar("Schedule Running", "success");
        rootStore.scheduleTableStore.updateScheduleById(result.data as Schedule)
        rootStore.scheduleTableStore.setConfirmResumeModalOpen(false);
        rootStore.scheduleEditStore.setStatus(ScheduleStatus.Running)
        rootStore.scheduleEditStore.setConfirmResumeModalOpen(false);
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => {
          rootStore.scheduleTableStore.setLoading(false)
          rootStore.scheduleEditStore.setLoading(false)
        });
    }).catch(error => {
      handleError(error)
      rootStore.scheduleTableStore.setLoading(false)
      rootStore.scheduleEditStore.setLoading(false)
    })
  }

  const createSchedule = (createScheduleRequest: CreateScheduleRequest) => {
    rootStore.generateScheduleStore.setLoading(true);
    authToken.then((token) => {
      client.post(`${config.bffBaseUrl}/schedule`, createScheduleRequest, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((response) => {
        snackbar.addSnackbar("Schedule Created", "success");
        navigate("/schedules/?scheduleId=" + (response.data as Schedule).id)
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => rootStore.generateScheduleStore.setLoading(false));
    }).catch(error => {
      handleError(error)
      rootStore.generateScheduleStore.setLoading(false)
    })
  }

  const updateSchedule = (updateScheduleRequest: UpdateScheduleRequest) => {
    rootStore.generateScheduleStore.setLoading(true);
    authToken.then((token) => {
      client.put(`${config.bffBaseUrl}/schedule`, updateScheduleRequest, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then((response) => {
        snackbar.addSnackbar("Schedule Updated", "success");
        rootStore.scheduleEditStore.reset()
        rootStore.scheduleEditStore.setServerSchedule(response.data as Schedule)
        rootStore.scheduleEditStore.setConfirmUpdateModalOpen(false)
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => rootStore.generateScheduleStore.setLoading(false));
    }).catch(error => {
      handleError(error)
      rootStore.generateScheduleStore.setLoading(false)
    })
  }

  const bulkUpdateSchedules = (updateScheduleRequest: BulkUpdateScheduleRequest) => {
    rootStore.bulkActionStore.setLoading(true);
    authToken.then((token) => {
      const params = createParams(rootStore.scheduleTableStore, !rootStore.scheduleTableStore.selectedAll);
      client.patch(`${config.bffBaseUrl}/schedule`, updateScheduleRequest, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
        params: params
      }).then((response) => {
        if (response.status === 202) {
          snackbar.addSnackbar("Bulk update partially successful, not all updates could be completed", "warning");
          rootStore.bulkActionStore.setLoading(false);
          rootStore.bulkActionStore.setUpdateInProgress(false)

          return;
        }
        setTimeout(() => {
          const data = response.data as BulkUpdateResponse;
          navigate("/schedules")
          rootStore.scheduleTableStore.resetSelection();
          snackbar.addSnackbar(`Batch update operation success. Affected ${data.totalModified} schedules`, "success");
          rootStore.bulkActionStore.setLoading(false)
        }, 1000)
      })
        .catch(error => {
          if (error instanceof AxiosError) {
            if (error.response?.status === 400) {
              const data = error.response.data as BulkUpdateResponse;
              if (data !== undefined) {
                snackbar.addSnackbar(`Bulk update failed, total affected: ${(error.response.data as BulkUpdateResponse).totalModified}\nFailure reasons: ${(error.response.data as BulkUpdateResponse).failureReasons.toString()}`, "error");
                return;
              } else {
                snackbar.addSnackbar("Bulk update failed", "error");
                return;
              }
            }
          }
          handleError(error)
          rootStore.bulkActionStore.setLoading(false)
          rootStore.bulkActionStore.setUpdateInProgress(false)
        })
    }).catch(error => {
      handleError(error)
      rootStore.bulkActionStore.setLoading(false)
      rootStore.bulkActionStore.setUpdateInProgress(false)
    })
  }

  const deleteScheduleById = (id: string) => {
    rootStore.scheduleTableStore.setLoading(true);
    rootStore.scheduleEditStore.setLoading(true);
    authToken.then((token) => {
      client.delete(`${config.bffBaseUrl}/schedule/${id}`, {
        headers: {
          "Authorization": "Bearer " + token.token,
        },
      }).then(() => {
        snackbar.addSnackbar("Schedule Deleted", "success");
        rootStore.scheduleTableStore.removeScheduleById(id)
        rootStore.scheduleTableStore.setLoading(false)
        rootStore.scheduleTableStore.setConfirmDeleteModalOpen(false);
        if (window.location.pathname === "/schedule/" + id) {
          setTimeout(() => {
            navigate("/schedules")
            rootStore.scheduleEditStore.setLoading(false)
          }, 1500)
        }
      })
        .catch(error => {
          handleError(error)
        })
        .finally(() => {
          rootStore.scheduleTableStore.setLoading(false)

        });
    }).catch(error => {
      handleError(error)
      rootStore.scheduleTableStore.setLoading(false)
      rootStore.scheduleEditStore.setLoading(false)
    })
  }

  return {
    getScheduleTable,
    pauseSchedule,
    getScheduleById,
    resumeSchedule,
    createSchedule,
    deleteScheduleById,
    updateSchedule,
    getScheduleReviewTable,
    bulkUpdateSchedules
  }
}
