import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, Typography} from "@mui/material";
import {useSearchParams} from "react-router-dom";
import TextFieldFilter from "../components/text_field_filter";
import {useStore} from "../context/StoreContext.tsx";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import LoadingButton from "@mui/lab/LoadingButton";
import RefreshRoundedIcon from "@mui/icons-material/RefreshRounded";
import StopCircleRoundedIcon from '@mui/icons-material/StopCircleRounded';

import StorageRoundedIcon from "@mui/icons-material/StorageRounded";
import GatewayFilter from "../components/gateway_filter/GatewayFilter.tsx";
import {observer} from "mobx-react-lite";
import ScheduleSendRoundIcon from "@mui/icons-material/ScheduleSend";
import PersonRoundedIcon from "@mui/icons-material/PersonRounded";
import HistoryStatusSelection from "../components/history_status_selection";
import {DataGrid} from "@mui/x-data-grid";
import useHistoryGridDef from "../components/history_table";
import {useEffect} from "react";
import {useHistoryService} from "../service/HistoryService.ts";
import {ConfirmModal, InspectHistoryModalDescription, StopCommunicationDescription} from "../components/modal";
import useQuery from "../helper/UseQuery.ts";
import {CommunicationStatus, ValidStatus} from "../models/History.ts";

const HistoryTable = observer(() => {
  const [, setSearchParams] = useSearchParams();
  const rootStore = useStore();
  const {historyTablePageColumns} = useHistoryGridDef();
  const {getHistoryPage, stopCommunication} = useHistoryService();


  const query = useQuery();
  useEffect(() => {
    const status = query.get("status");
    const gatewayId = query.get("gatewayId");
    const scheduleId = query.get("scheduleId");
    const userId = query.get("userId");
    const strictQuery = query.get("strict");
    if (strictQuery === "true") {
      rootStore.historyTableStore.setGatewayIdFilter("");
      rootStore.historyTableStore.setScheduleIdFilter("");
      rootStore.historyTableStore.setUserIdFilter("");
    }
    if (gatewayId !== null) {
      rootStore.historyTableStore.setGatewayIdFilter(gatewayId);
    }
    if (scheduleId !== null) {
      rootStore.historyTableStore.setScheduleIdFilter(scheduleId);
    }
    if (userId !== null) {
      rootStore.historyTableStore.setUserIdFilter(userId);
    }
    if (status !== null && ValidStatus.includes(status as CommunicationStatus)) {
      rootStore.historyTableStore.setStatusFilter([status as CommunicationStatus]);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    getHistoryPage();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.historyTableStore.paginationModel, rootStore.historyTableStore.statusFilter]);


  return (
    <>
      <ConfirmModal open={rootStore.historyTableStore.openConfirmStopModal}
                    setOpen={rootStore.historyTableStore.setOpenConfirmStopModal}
                    heading={"Are you sure you want to stop this communication?"}
                    description={<StopCommunicationDescription/>}
                    confirmIcon={<StopCircleRoundedIcon/>}
                    confirmText={"Stop Communication"}
                    idPrefix={"stop-com-"}
                    onConfirm={() => {
                      stopCommunication()
                    }}
                    loading={rootStore.historyTableStore.isLoading}
      />
      <ConfirmModal open={rootStore.historyTableStore.openInspectModal}
                    setOpen={rootStore.historyTableStore.setOpenInspectModal}
                    heading={"View Communication"}
                    description={<InspectHistoryModalDescription/>}
                    showConfirmButton={false}
                    cancelText={"Close"}
                    loading={rootStore.historyTableStore.isLoading}
      />

      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12} mb={2}>
          <Typography variant="h1" fontSize={"4rem"} id={"history-page-heading"}>Communication History</Typography>
        </Grid>

        <Grid mdOffset={5} xlOffset={9} xs={12} container spacing={0} mb={0}>
          <Grid xs={6} lg={6} sx={{pr: 1}}>
            <Button variant="contained" fullWidth color="info" endIcon={<CloseRoundedIcon/>}
                    onClick={() => {
                      rootStore.historyTableStore.resetFilters();
                      setSearchParams(new URLSearchParams());
                      getHistoryPage();
                    }}
                    disabled={rootStore.historyTableStore.statusFilter.join() === ["Any Status"].join() && rootStore.historyTableStore.gatewayIdFilter === "" && rootStore.historyTableStore.scheduleIdFilter === "" && rootStore.historyTableStore.userIdFilter === ""}
                    id={"reset-filters-button"}
            >Reset Filters</Button>
          </Grid>
          <Grid xs={6} lg={6} sx={{pl: 1}}>
            <LoadingButton loading={rootStore.historyTableStore.isLoading} variant="contained" fullWidth
                           color="secondary" endIcon={<RefreshRoundedIcon/>}
                           id={"refresh-history-button"}
                           onClick={() => getHistoryPage()} loadingPosition="end"
            >Refresh</LoadingButton>
          </Grid>
        </Grid>

        <Grid xs={12} lg={6} xl={2}>
          <HistoryStatusSelection/>
        </Grid>

        <Grid xs={12} lg={6} xl={4}>
          <GatewayFilter fieldValue={rootStore.historyTableStore.gatewayIdFilter}
                         setFieldValue={rootStore.historyTableStore.setGatewayIdFilter}
                         isFieldFocused={rootStore.historyTableStore.gatewayIdFocus}
                         setIsFieldFocused={rootStore.historyTableStore.setGatewayIdFocus}
                         idPrefix={"gateway"}
                         queryParam={"gatewayId"}
                         label={"Gateway"}
                         InputIcon={StorageRoundedIcon}
                         fetchResults={getHistoryPage}
          />
        </Grid>

        <Grid xs={12} lg={6} xl={3}>
          <TextFieldFilter fieldValue={rootStore.historyTableStore.scheduleIdFilter}
                           setFieldValue={rootStore.historyTableStore.setScheduleIdFilter}
                           isFieldFocused={rootStore.historyTableStore.scheduleIdFocus}
                           setIsFieldFocused={rootStore.historyTableStore.setScheduleIdFocus}
                           idPrefix={"schedule-id"}
                           queryParam={"scheduleId"}
                           label={"Schedule ID"}
                           InputIcon={ScheduleSendRoundIcon}
                           fetchResults={getHistoryPage}
                           zIndex={40}
          />
        </Grid>

        <Grid xs={12} lg={6} xl={3}>
          <TextFieldFilter fieldValue={rootStore.historyTableStore.userIdFilter}
                           setFieldValue={rootStore.historyTableStore.setUserIdFilter}
                           isFieldFocused={rootStore.historyTableStore.userIdFocus}
                           setIsFieldFocused={rootStore.historyTableStore.setUserIdFocus}
                           idPrefix={"user-id"}
                           queryParam={"userId"}
                           label={"User ID"}
                           InputIcon={PersonRoundedIcon}
                           fetchResults={getHistoryPage}
                           zIndex={30}
          />
        </Grid>

        <Grid xs={12}>
          <DataGrid columns={historyTablePageColumns} rows={rootStore.historyTableStore.historyTableData.slice()}
                    autoHeight
                    initialState={{
                      pagination: {
                        paginationModel: rootStore.historyTableStore.paginationModel
                        ,
                      },
                      columns: {
                        columnVisibilityModel: {
                          gatewayId: false,
                        }
                      }
                    }}
                    disableRowSelectionOnClick
                    disableColumnSorting
                    pageSizeOptions={[5, 10, 25, 50, 100]}
                    paginationModel={rootStore.historyTableStore.paginationModel}
                    paginationMode={'server'}
                    rowCount={rootStore.historyTableStore.totalCount}
                    loading={rootStore.historyTableStore.isLoading}
                    onPaginationModelChange={(paginationModel) => {
                      rootStore.historyTableStore.setPaginationModel(paginationModel);
                    }}
          />
        </Grid>


      </Grid>
    </>
  )
})

export default HistoryTable;
