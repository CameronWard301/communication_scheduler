import {observer} from "mobx-react-lite";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, Typography} from "@mui/material";
import ArrowBackRoundedIcon from "@mui/icons-material/ArrowBackRounded";
import {useNavigate} from "react-router-dom";
import {BulkActionStepper} from "../../components/progress_steps";
import {useStore} from "../../context/StoreContext.tsx";
import {ActionItem, ActionPanelCentre} from "../../components/action_pannel";
import StorageRoundedIcon from "@mui/icons-material/StorageRounded";
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import PlayCircleOutlineRoundedIcon from '@mui/icons-material/PlayCircleOutlineRounded';
import PauseCircleOutlineRoundedIcon from '@mui/icons-material/PauseCircleOutlineRounded';
import CheckRoundedIcon from '@mui/icons-material/CheckRounded';
import {BulkActionType, BulkUpdateScheduleRequest} from "../../models/Schedules.ts";
import ReviewBulkAction from "../../components/review_bulk_action";
import {ConfirmModal} from "../../components/modal";
import {ConfirmBulkAction} from "../../components/modal/schedule";
import {useScheduleService} from "../../service/ScheduleService.ts";
import {SelectGatewaySchedule} from "../../components/select_gateway_schedule";

const BulkScheduleAction = observer(() => {
  const navigate = useNavigate();
  const rootStore = useStore();
  const {bulkUpdateSchedules} = useScheduleService();

  const handleBulkUpdate = () => {
    rootStore.bulkActionStore.setUpdateInProgress(true)
    const request: BulkUpdateScheduleRequest = {
      actionType: rootStore.bulkActionStore.bulkActionType!
    }
    if (rootStore.bulkActionStore.bulkActionType == BulkActionType.Gateway) {
      request.gatewayId = rootStore.bulkActionStore.selectedGateway?.id;
    }
    bulkUpdateSchedules(request);
  }

  return (
    <>
      <ConfirmModal open={rootStore.bulkActionStore.isConfirmModalOpen}
                    setOpen={rootStore.bulkActionStore.setConfirmModalOpen}
                    heading={"Are you sure you want to apply the following action to the selected schedules?"}
                    description={<ConfirmBulkAction/>}
                    confirmIcon={<CheckRoundedIcon/>}
                    confirmText={"Apply Action"}
                    onConfirm={() => handleBulkUpdate()}
                    loading={rootStore.bulkActionStore.isLoading}/>
      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12}>
          <Button sx={{mb: 1}} variant="contained" color="primary" onClick={() => navigate("/schedules")}
                  startIcon={<ArrowBackRoundedIcon/>}>Back to schedules</Button>
          <Typography variant="h1" fontSize={"4rem"} id={"schedule-action-page-heading"}>Schedule Bulk
            Actions</Typography>
          <Typography variant="body1" id={"schedule-action-subtitle"}>Modify multiple schedules at once</Typography>
        </Grid>
        <BulkActionStepper>
          <>
            {
              rootStore.bulkActionStore.currentStep == 1 && (
                <ActionPanelCentre minHeight={"50vh"}>
                  <ActionItem title={"Update Gateway"}
                              onClick={() => rootStore.bulkActionStore.setBulkActionType(BulkActionType.Gateway)}
                              selected={rootStore.bulkActionStore.bulkActionType == BulkActionType.Gateway}
                              size={"large"}
                              Icon={StorageRoundedIcon}
                              key={"gateway"}
                              componentName={"gateway"}
                              subtitle={"Update schedules' gateways"}>

                  </ActionItem>
                  <ActionItem title={"Pause Schedules"}
                              onClick={() => rootStore.bulkActionStore.setBulkActionType(BulkActionType.Pause)}
                              selected={rootStore.bulkActionStore.bulkActionType == BulkActionType.Pause}
                              size={"large"}
                              Icon={PauseCircleOutlineRoundedIcon}
                              key={"pause-schedules"}
                              componentName={"pause-schedules"}
                              subtitle={"Pause all selected schedules"}>

                  </ActionItem>
                  <ActionItem title={"Resume Schedules"}
                              onClick={() => rootStore.bulkActionStore.setBulkActionType(BulkActionType.Resume)}
                              selected={rootStore.bulkActionStore.bulkActionType == BulkActionType.Resume}
                              size={"large"}
                              Icon={PlayCircleOutlineRoundedIcon}
                              key={"resume-schedules"}
                              componentName={"resume-schedules"}
                              subtitle={"Resume all selected schedules"}>

                  </ActionItem>
                  <ActionItem title={"Delete Schedules"}
                              onClick={() => rootStore.bulkActionStore.setBulkActionType(BulkActionType.Delete)}
                              selected={rootStore.bulkActionStore.bulkActionType == BulkActionType.Delete}
                              size={"large"}
                              Icon={DeleteRoundedIcon}
                              key={"delete-schedules"}
                              componentName={"delete-schedules"}
                              subtitle={"Delete all selected schedules"}>

                  </ActionItem>
                </ActionPanelCentre>
              )
            }
            {
              (rootStore.bulkActionStore.currentStep == 3 || (rootStore.bulkActionStore.currentStep == 2 && rootStore.bulkActionStore.bulkActionType !== BulkActionType.Gateway)) && (
                <ReviewBulkAction/>
              )
            }
            {
              rootStore.bulkActionStore.currentStep == 2 && rootStore.bulkActionStore.bulkActionType == BulkActionType.Gateway && (
                <>
                  <SelectGatewaySchedule selectionModel={rootStore.bulkActionStore.gatewaySelectionModel}
                                         setSelectionModel={rootStore.bulkActionStore.setGatewaySelectionModel}
                                         setSelectedGateway={rootStore.bulkActionStore.setSelectedGateway}/>
                </>
              )
            }
          </>
        </BulkActionStepper>
      </Grid>
    </>
  )
})

export default BulkScheduleAction;
