import {observer} from "mobx-react-lite";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Typography} from "@mui/material";
import {useStore} from "../../context/StoreContext.tsx";
import {DataGrid} from "@mui/x-data-grid";
import useScheduleGridDef from "../schedule_table/useScheduleGridDef.tsx";
import {useScheduleService} from "../../service/ScheduleService.ts";
import {useEffect} from "react";
import {ReviewGatewayTable} from "../modal/gateway";
import Box from "@mui/material/Box";
import {BulkActionType} from "../../models/Schedules.ts";
import LoadingButton from "@mui/lab/LoadingButton";
import {RefreshRounded} from "@mui/icons-material";

const ReviewBulkAction = observer(() => {
  const rootStore = useStore();
  const {columns} = useScheduleGridDef();
  const {getScheduleReviewTable} = useScheduleService();

  useEffect(() => {
    rootStore.scheduleTableStore.setPaginationModel({
      pageSize: 100,
      page: 0,
    });
    getScheduleReviewTable();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      <Grid xs={12}>
        <Typography variant={"h4"}><b>Review</b></Typography>
        <Typography variant={"h5"}><b>Action:</b> <span
          id={"bulk-action-review-type"}>{rootStore.bulkActionStore.bulkActionType}</span></Typography>
        <Typography variant={"h5"}><b>Selected Schedules:</b> <span
          id={"selected-schedule-number"}>{rootStore.scheduleTableStore.scheduleTableData.length}</span></Typography>
        <Typography sx={{display: "inline-block"}} variant={"h5"} mt={2}><b>The following schedules will be
          affected:</b></Typography>
        <LoadingButton id={"refresh-selection"} variant={"contained"} endIcon={<RefreshRounded/>} sx={{ml: 3, mb: 1}}
                       loading={rootStore.scheduleTableStore.isLoading}
                       onClick={() => getScheduleReviewTable()}>Refresh</LoadingButton>
        <DataGrid columns={columns} rows={rootStore.scheduleTableStore.scheduleTableData.slice()}
                  autoHeight
                  sx={{mt: 1}}
                  initialState={{
                    pagination: {
                      paginationModel: rootStore.scheduleTableStore.paginationModel,
                    },
                    columns: {
                      columnVisibilityModel: {
                        createdAt: false,
                        updatedAt: false,
                        actions: false,
                      }
                    }
                  }}
                  disableColumnMenu
                  pageSizeOptions={[5, 10, 25, 50, 100, 200, 500, 1000]}
                  paginationModel={rootStore.scheduleTableStore.paginationModel}
                  paginationMode={rootStore.scheduleTableStore.selectedAll ? 'server' : 'client'}
                  sortingMode={rootStore.scheduleTableStore.selectedAll ? 'server' : 'client'}
                  disableRowSelectionOnClick
                  keepNonExistentRowsSelected
                  rowCount={rootStore.scheduleTableStore.totalCount}
                  loading={rootStore.scheduleTableStore.isLoading}
                  onPaginationModelChange={(paginationModel) => {
                    rootStore.scheduleTableStore.setPaginationModel(paginationModel);
                  }}
        />
        {rootStore.bulkActionStore.bulkActionType === BulkActionType.Gateway && (
          <>
            <Typography variant={"h5"} mt={2}><b>New Gateway:</b></Typography>
            <Box sx={{width: {xs: "100%", md: "50%"}, my: 2}}>
              <ReviewGatewayTable gateway={rootStore.bulkActionStore.selectedGateway!}/>
            </Box>
          </>

        )}

      </Grid>
    </>
  )
})

export default ReviewBulkAction;
