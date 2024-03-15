import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, Typography} from "@mui/material";
import {DataGrid, GridEventListener, useGridApiRef} from "@mui/x-data-grid";
import {useStore} from "../../context/StoreContext.tsx";
import useScheduleGridDef from "../../components/schedule_table/useScheduleGridDef.tsx";
import {observer} from "mobx-react-lite";
import {useScheduleService} from "../../service/ScheduleService.ts";
import {useEffect} from "react";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import LoadingButton from "@mui/lab/LoadingButton";
import RefreshRoundedIcon from "@mui/icons-material/RefreshRounded";
import {useNavigate, useSearchParams} from "react-router-dom";
import TextFieldFilter from "../../components/text_field_filter";
import ScheduleSendRoundIcon from "@mui/icons-material/ScheduleSend";
import StorageRoundedIcon from '@mui/icons-material/StorageRounded';
import PauseCircleFilledRoundedIcon from '@mui/icons-material/PauseCircleFilledRounded';
import PersonRoundedIcon from '@mui/icons-material/PersonRounded';
import PlayCircleFilledRoundedIcon from '@mui/icons-material/PlayCircleFilledRounded';
import GatewayFilter from "../../components/gateway_filter/GatewayFilter.tsx";
import {useGatewayService} from "../../service/GatewayService.ts";
import {ConfirmModal} from "../../components/modal";
import {ConfirmPauseResumeSchedule} from "../../components/modal/schedule";
import AddCircleOutlineRoundedIcon from "@mui/icons-material/AddCircleOutlineRounded";
import useQuery from "../../helper/UseQuery.ts";

const ScheduleTable = observer(() => {
  const rootStore = useStore();
  const {columns} = useScheduleGridDef();
  const {getScheduleTable, pauseSchedule, resumeSchedule} = useScheduleService();
  const {getGatewaysForScheduleFilter} = useGatewayService();
  const apiRef = useGridApiRef();
  const [, setSearchParams] = useSearchParams();
  const navigate = useNavigate();

  const query = useQuery();
  useEffect(() => {
    const gatewayId = query.get("gatewayId");
    const scheduleId = query.get("scheduleId");
    const userId = query.get("userId");
    if (gatewayId !== null) {
      rootStore.scheduleTableStore.setGatewayIdFilter(gatewayId);
    }
    if (scheduleId !== null) {
      rootStore.scheduleTableStore.setScheduleIdFilter(scheduleId);
    }
    if (userId !== null) {
      rootStore.scheduleTableStore.setUserIdFilter(userId);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);


  useEffect(() => {
    getScheduleTable();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.scheduleTableStore.paginationModel, rootStore.scheduleTableStore.sortModel, rootStore.scheduleTableStore.gatewayIdFilter]);

  useEffect(() => {
    const handleEvent: GridEventListener<'headerSelectionCheckboxChange'> = (
      params,
    ) => {
      rootStore.scheduleTableStore.setSelectAll(params.value)
    }

    apiRef.current.subscribeEvent('headerSelectionCheckboxChange', handleEvent);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiRef]);

  const pauseScheduleAction = () =>{
    pauseSchedule(rootStore.scheduleTableStore.selectedSchedule.id);
  }

  const resumeScheduleAction = () =>{
    resumeSchedule(rootStore.scheduleTableStore.selectedSchedule.id);
  }

  return (
    <>
      <ConfirmModal open={rootStore.scheduleTableStore.confirmPauseModalOpen}
                    setOpen={rootStore.scheduleTableStore.setConfirmPauseModalOpen}
                    heading={"Are you sure you want to pause this schedule?"}
                    description={<ConfirmPauseResumeSchedule scheduleId={rootStore.scheduleTableStore.selectedSchedule.id}/>}
                    confirmIcon={<PauseCircleFilledRoundedIcon/>}
                    confirmText={"Pause Schedule"}
                    onConfirm={pauseScheduleAction}
                    loading={rootStore.scheduleTableStore.isLoading}/>
      <ConfirmModal open={rootStore.scheduleTableStore.confirmResumeModalOpen}
                    setOpen={rootStore.scheduleTableStore.setConfirmResumeModalOpen}
                    heading={"Are you sure you want to run this schedule?"}
                    description={<ConfirmPauseResumeSchedule scheduleId={rootStore.scheduleTableStore.selectedSchedule.id}/>}
                    confirmIcon={<PlayCircleFilledRoundedIcon/>}
                    confirmText={"Run Schedule"}
                    onConfirm={resumeScheduleAction}
                    loading={rootStore.scheduleTableStore.isLoading}/>

      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12}>
          <Typography variant="h1" fontSize={"4rem"} id={"schedule-page-heading"}>Communication Schedules</Typography>
        </Grid>
        <Grid xs={12} mdOffset={9} md={3} >
          <Button id={"create-new-schedule"} variant={"contained"}
                  fullWidth
                  endIcon={<AddCircleOutlineRoundedIcon/>}
                  onClick={() => navigate("/add-schedule")}
          >Add New Schedule</Button>
        </Grid>

        <Grid xs={3}>
          <TextFieldFilter fieldValue={rootStore.scheduleTableStore.scheduleIdFilter}
                           setFieldValue={rootStore.scheduleTableStore.setScheduleIdFilter}
                           isFieldFocused={rootStore.scheduleTableStore.scheduleIdFocus}
                           setIsFieldFocused={rootStore.scheduleTableStore.setScheduleIdFocus}
                           idPrefix={"schedule-id"}
                           queryParam={"scheduleId"}
                           label={"Schedule ID"}
                           InputIcon={ScheduleSendRoundIcon}
                           fetchResults={getScheduleTable}
          />
        </Grid>

        <Grid xs={6}>
          <GatewayFilter fieldValue={rootStore.scheduleTableStore.gatewayIdFilter}
                           setFieldValue={rootStore.scheduleTableStore.setGatewayIdFilter}
                           isFieldFocused={rootStore.scheduleTableStore.gatewayIdFocus}
                           setIsFieldFocused={rootStore.scheduleTableStore.setGatewayIdFocus}
                           idPrefix={"gateway"}
                           queryParam={"gatewayId"}
                           label={"Gateway"}
                           InputIcon={StorageRoundedIcon}
                           fetchResults={getGatewaysForScheduleFilter}
          />
        </Grid>

        <Grid xs={3}>
          <TextFieldFilter fieldValue={rootStore.scheduleTableStore.userIdFilter}
                           setFieldValue={rootStore.scheduleTableStore.setUserIdFilter}
                           isFieldFocused={rootStore.scheduleTableStore.userIdFocus}
                           setIsFieldFocused={rootStore.scheduleTableStore.setUserIdFocus}
                           idPrefix={"user-id"}
                           queryParam={"userId"}
                           label={"User ID"}
                           InputIcon={PersonRoundedIcon}
                           fetchResults={getScheduleTable}
          />
        </Grid>

        <Grid mdOffset={9} xs={12} container spacing={0} mb={0}>
          <Grid xs={6} sx={{pr: 1}}>
            <Button variant="contained" fullWidth color="info" endIcon={<CloseRoundedIcon/>}
                    disabled={rootStore.scheduleTableStore.userIdFilter === "" && rootStore.scheduleTableStore.gatewayIdFilter === "" && rootStore.scheduleTableStore.scheduleIdFilter === ""}
                    onClick={() => {
                      rootStore.scheduleTableStore.resetFilters();
                      setSearchParams(new URLSearchParams());
                      getScheduleTable();
                    }}
                    id={"reset-filters-button"}
            >Reset Filters</Button>
          </Grid>
          <Grid xs={6} sx={{pl: 1}}>
            <LoadingButton loading={rootStore.scheduleTableStore.isLoading} variant="contained" fullWidth
                           color="secondary" endIcon={<RefreshRoundedIcon/>}
                           onClick={() => getScheduleTable()} loadingPosition="end"
            >Refresh</LoadingButton>
          </Grid>
        </Grid>

        <Grid xs={12}>
          <DataGrid columns={columns} rows={rootStore.scheduleTableStore.scheduleTableData.slice()}
                    autoHeight
                    initialState={{
                      pagination: {
                        paginationModel: rootStore.scheduleTableStore.paginationModel,
                      },
                      columns: {
                        columnVisibilityModel: {
                          createdAt: false,
                          updatedAt: false,
                        }
                      }
                    }}
                    pageSizeOptions={[5, 10, 25, 50, 100]}
                    paginationModel={rootStore.scheduleTableStore.paginationModel}
                    paginationMode={'server'}
                    sortingMode={'server'}
                    disableRowSelectionOnClick
                    checkboxSelection
                    keepNonExistentRowsSelected
                    rowCount={rootStore.scheduleTableStore.totalCount}
                    rowSelectionModel={rootStore.scheduleTableStore.checkBoxSelectionModel}
                    loading={rootStore.scheduleTableStore.isLoading}
                    onPaginationModelChange={(paginationModel) => {
                      rootStore.scheduleTableStore.setPaginationModel(paginationModel);
                    }}
                    onSortModelChange={(sortModel) => {
                      rootStore.scheduleTableStore.setSortModel(sortModel);
                    }}
                    onRowSelectionModelChange={(selectionModel) => {
                      rootStore.scheduleTableStore.setCheckBoxSelectionModel(selectionModel);
                    }}
                    apiRef={apiRef}

          >

          </DataGrid>
        </Grid>
      </Grid>
    </>
  )
})

export default ScheduleTable
