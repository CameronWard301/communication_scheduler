import {observer} from "mobx-react-lite";
import {useContext, useEffect} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {useScheduleService} from "../../service/ScheduleService.ts";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, CircularProgress, Paper, TextField, Typography, useTheme} from "@mui/material";
import ArrowBackRoundedIcon from "@mui/icons-material/ArrowBackRounded";
import {useStore} from "../../context/StoreContext.tsx";
import Box from "@mui/material/Box";
import CustomTooltip from "../../components/tooltip";
import {ReviewGatewayTable} from "../../components/modal/gateway";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import {ConfirmModal} from "../../components/modal";
import SyncRoundedIcon from '@mui/icons-material/SyncRounded';
import {SelectGatewaySchedule} from "../../components/select_gateway_schedule";
import {CreateSchedule} from "../../components/create_schedule";
import CloseIcon from "@mui/icons-material/Close";
import DeleteRounded from "@mui/icons-material/DeleteRounded";
import DeleteRoundedIcon from "@mui/icons-material/DeleteRounded";
import LoadingButton from "@mui/lab/LoadingButton";
import SaveRoundedIcon from "@mui/icons-material/SaveRounded";
import {ScheduleStatus, ScheduleType, UpdateScheduleRequest} from "../../models/Schedules.ts";
import {PauseCircleRounded, PlayCircleRounded} from "@mui/icons-material";
import {ConfirmModifySchedule, ScheduleChangesTable} from "../../components/modal/schedule";
import PauseCircleFilledRoundedIcon from "@mui/icons-material/PauseCircleFilledRounded";
import PlayCircleFilledRoundedIcon from "@mui/icons-material/PlayCircleFilledRounded";
import {UpcomingCommunications} from "../../components/upcoming_communications";
import {SnackbarContext} from "../../context/SnackbarContext.tsx";


const EditSchedule = observer(() => {
  const params = useParams();
  const {getScheduleById, pauseSchedule, resumeSchedule, deleteScheduleById, updateSchedule} = useScheduleService();
  const navigate = useNavigate();
  const rootStore = useStore();
  const theme = useTheme();
  const snackbar = useContext(SnackbarContext);

  useEffect(() => {
    if (params.id) {
      getScheduleById(params.id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [params.id]);

  const handleConfirmChangeGateway = () => {
    rootStore.scheduleEditStore.setUpdatedGateway(rootStore.gatewayTableStore.selectedGateway);
    rootStore.scheduleEditStore.setChangeGatewayModalOpen(false);
  }

  const handleConfirmChangeSchedule = () => {
    rootStore.scheduleEditStore.setUpdatingScheduleSpec(
      rootStore.generateScheduleStore.selectedScheduleType,
      rootStore.generateScheduleStore.intervalSpec,
      rootStore.generateScheduleStore.calendarWeekSpec,
      rootStore.generateScheduleStore.calendarMonthSpec,
      rootStore.generateScheduleStore.cronString);
    rootStore.scheduleEditStore.setChangeScheduleModalOpen(false);
  }

  const pauseScheduleAction = () => {
    pauseSchedule(rootStore.scheduleEditStore.serverSchedule!.id, rootStore.scheduleEditStore.serverSchedule!.gatewayName);
  }

  const resumeScheduleAction = () => {
    resumeSchedule(rootStore.scheduleEditStore.serverSchedule!.id, rootStore.scheduleEditStore.serverSchedule!.gatewayName);
  }

  const deleteScheduleAction = () => {
    deleteScheduleById(rootStore.scheduleEditStore.serverSchedule!.id);
  }

  const isScheduleSpecValid = () => {
    if (rootStore.generateScheduleStore.selectedScheduleType === ScheduleType.Interval && (!rootStore.createScheduleStore.isIntervalFieldsValid() || rootStore.createScheduleStore.isIntervalFieldsAllZero() || !rootStore.createScheduleStore.isIntervalOffsetValid())) {
      rootStore.generateScheduleStore.setAllowNext(false);
      return false;
    }
    if ((rootStore.generateScheduleStore.selectedScheduleType === ScheduleType.CalendarMonth || rootStore.generateScheduleStore.selectedScheduleType === ScheduleType.CalendarWeek) && !rootStore.createScheduleStore.isMonthTypeValid() || !rootStore.createScheduleStore.isDayOfMonthValid()) {
      rootStore.generateScheduleStore.setAllowNext(false);
      return false;
    }
    return !(rootStore.generateScheduleStore.selectedScheduleType == ScheduleType.Cron && (!rootStore.createScheduleStore.isCronStringValid() || rootStore.createScheduleStore.cronParseError));

  }
  const handleModifyModal = () => {
    if ((rootStore.scheduleEditStore.serverSchedule?.userId === rootStore.scheduleEditStore.updatedSchedule?.userId) &&
      (rootStore.scheduleEditStore.serverSchedule?.gateway.id === rootStore.scheduleEditStore.updatedSchedule?.gateway.id) &&
      (rootStore.scheduleEditStore.updateScheduleSpec === null)
    ) {
      snackbar.addSnackbar("No changes to save, please edit a field and try again", "info");
      return
    }
    rootStore.scheduleEditStore.setConfirmUpdateModalOpen(true);
  }

  const updateScheduleAction = () => {
    const scheduleUpdate = {
      scheduleId: rootStore.scheduleEditStore.serverSchedule!.id,
      cronSpec: rootStore.scheduleEditStore.updateScheduleSpec?.cronSpec,
      intervalSpec: rootStore.scheduleEditStore.updateScheduleSpec?.intervalSpec,
      calendarWeekSpec: rootStore.scheduleEditStore.updateScheduleSpec?.calendarWeekSpec,
      calendarMonthSpec: rootStore.scheduleEditStore.updateScheduleSpec?.calendarMonthSpec,
      scheduleType: rootStore.scheduleEditStore.updateScheduleSpec?.scheduleType,
      gatewayId: rootStore.scheduleEditStore.updatedSchedule?.gateway.id,
      userId: rootStore.scheduleEditStore.updatedSchedule?.userId,
    } as UpdateScheduleRequest;
    updateSchedule(scheduleUpdate);
  }

  return (
    <>
      {
        params.id && (
          <>
            <ConfirmModal open={rootStore.scheduleEditStore.confirmPauseModalOpen}
                          setOpen={rootStore.scheduleEditStore.setConfirmPauseModalOpen}
                          heading={"Are you sure you want to pause this schedule?"}
                          description={<ConfirmModifySchedule scheduleId={params.id}/>}
                          confirmIcon={<PauseCircleFilledRoundedIcon/>}
                          confirmText={"Pause Schedule"}
                          onConfirm={pauseScheduleAction}
                          loading={rootStore.scheduleEditStore.loading}/>
            <ConfirmModal open={rootStore.scheduleEditStore.confirmResumeModalOpen}
                          setOpen={rootStore.scheduleEditStore.setConfirmResumeModalOpen}
                          heading={"Are you sure you want to run this schedule?"}
                          description={<ConfirmModifySchedule scheduleId={params.id}/>}
                          confirmIcon={<PlayCircleFilledRoundedIcon/>}
                          confirmText={"Run Schedule"}
                          onConfirm={resumeScheduleAction}
                          loading={rootStore.scheduleEditStore.loading}/>
            <ConfirmModal open={rootStore.scheduleEditStore.deleteScheduleModalOpen}
                          setOpen={rootStore.scheduleEditStore.setDeleteScheduleModalOpen}
                          heading={"Are you sure you want to delete this schedule?"}
                          description={<ConfirmModifySchedule scheduleId={params.id}/>}
                          confirmIcon={<DeleteRoundedIcon/>}
                          confirmText={"Delete Schedule"}
                          onConfirm={deleteScheduleAction}
                          loading={rootStore.scheduleEditStore.loading}/>
            <ConfirmModal open={rootStore.scheduleEditStore.confirmUpdateModalOpen}
                          setOpen={rootStore.scheduleEditStore.setConfirmUpdateModalOpen}
                          heading={"Are you sure you want to modify this schedule?"}
                          description={<ScheduleChangesTable/>}
                          confirmIcon={<SaveRoundedIcon/>}
                          confirmText={"Modify Schedule"}
                          onConfirm={updateScheduleAction}
                          loading={rootStore.scheduleEditStore.loading}/>
          </>
        )
      }

      <ConfirmModal open={rootStore.scheduleEditStore.changeGatewayModalOpen}
                    setOpen={rootStore.scheduleEditStore.setChangeGatewayModalOpen}
                    heading={""}
                    description={<SelectGatewaySchedule
                      selectionModel={rootStore.scheduleEditStore.gatewaySelectionModel}
                      setSelectedGateway={rootStore.gatewayTableStore.setSelectedGateway}
                      setSelectionModel={rootStore.scheduleEditStore.setGatewaySelectionModel}/>}
                    confirmIcon={<SyncRoundedIcon/>}
                    confirmText={"Change Gateway"}
                    descriptionContainer={false}
                    onConfirm={handleConfirmChangeGateway}
                    allowConfirm={rootStore.scheduleEditStore.gatewaySelectionModel.length !== 0}
                    confirmErrorMessage={"Please select a gateway from the table"}
                    loading={rootStore.scheduleEditStore.loading}/>
      <ConfirmModal open={rootStore.scheduleEditStore.changeScheduleModalOpen}
                    setOpen={rootStore.scheduleEditStore.setChangeScheduleModalOpen}
                    heading={"Create a new schedule"}
                    height={"93vh"}
                    width={"90%"}
                    fixButtonsBottom={true}
                    description={<CreateSchedule
                      selectedScheduleType={rootStore.generateScheduleStore.selectedScheduleType}
                      setCalendarMonthSpec={rootStore.generateScheduleStore.setCalendarMonthSpec}
                      setCalendarWeekSpec={rootStore.generateScheduleStore.setCalendarWeekSpec}
                      setIntervalSpec={rootStore.generateScheduleStore.setIntervalSpec}
                      setSelectedScheduleType={rootStore.generateScheduleStore.setSelectedScheduleType}
                      setCronString={rootStore.generateScheduleStore.setCronString}

                    />}
                    confirmIcon={<SyncRoundedIcon/>}
                    confirmText={"Change Schedule"}
                    descriptionContainer={false}
                    onConfirm={handleConfirmChangeSchedule}
                    allowConfirm={isScheduleSpecValid()}
                    confirmErrorMessage={"Please create a valid schedule"}
                    loading={rootStore.scheduleEditStore.loading}/>

      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12} mb={2}>
          <Button sx={{mb: 1}} variant="contained" color="primary" id={"go-back"} onClick={() => navigate(-1)}
                  startIcon={<ArrowBackRoundedIcon/>}>Go back</Button>
          <Typography variant="h1" fontSize={"4rem"} id={"schedule-edit-page-heading"}>Edit Communication
            Schedule</Typography>
        </Grid>
        {rootStore.scheduleEditStore.loading && (
          <Grid xs={12}>
            <Box sx={{display: 'flex', justifyContent: 'center'}}>
              <CircularProgress/>
            </Box>
          </Grid>
        )}
        {rootStore.scheduleEditStore.serverSchedule === null && !rootStore.scheduleEditStore.loading && (
          <Grid xs={12}>
            <Typography variant={"h6"}>Could not find schedule with id: {params.id}</Typography>
          </Grid>
        )}
        {rootStore.scheduleEditStore.serverSchedule !== null && rootStore.scheduleEditStore.updatedSchedule !== null && !rootStore.scheduleEditStore.loading && (
          <>
            <Grid xs={12}>
              <Typography variant={"body1"}
                          id={"schedule-id"}><strong>ID: </strong>{rootStore.scheduleEditStore.updatedSchedule.id}
              </Typography>
              <Typography variant={"body1"} id={"date-created"}><strong>Date
                Created: </strong>{rootStore.scheduleEditStore.updatedSchedule.createdAt}</Typography>
              <Typography variant={"body1"} id={"status"}><strong>Status: </strong><span
                style={{color: rootStore.scheduleEditStore.serverSchedule.status == ScheduleStatus.Running ? theme.palette.success.main : theme.palette.warning.main}}>{rootStore.scheduleEditStore.serverSchedule.status}</span></Typography>
            </Grid>
            <Grid xs={12} container spacing={2}>
              <Grid xs={12} lg={6} m={1} ml={2}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"} id={"schedule-user-id"}>User Id<span
                    style={{color: theme.palette.error.main}}>*</span></Typography>
                  <CustomTooltip message="The user ID of the user to send the communication to" ariaLabelTopic={"User ID"}/>
                </Box>
                <Box>
                  <TextField label="User Id" type="text" variant="outlined" margin={"normal"}
                             fullWidth
                             required
                             error={!rootStore.scheduleEditStore.isUserIdValid()}
                             helperText={!rootStore.scheduleEditStore.isUserIdValid() ? "Please enter a user Id" : ""}
                             value={rootStore.scheduleEditStore.updatedSchedule.userId} id={"user-id-input"}
                             onChange={(event) => rootStore.scheduleEditStore.setUserId(event.target.value)}/>
                </Box>
              </Grid>
              <Grid xs={12}></Grid>
              <Grid xs={12} lg={5} m={1} ml={2}>
                <Box display={"flex"} alignItems={"center"}>
                  <Typography variant={"h4"} display={"inline-block"} id={"connected-gateway-title"}>Connected
                    Gateway</Typography>
                  <CustomTooltip message="The gateway that will process the communication for this schedule" ariaLabelTopic={"Connected gateway"}/>
                  <Button variant={"contained"} color={"secondary"} endIcon={<SyncRoundedIcon/>} sx={{ml: 3}}
                          id={"change-gateway-button"}
                          onClick={() => {
                            rootStore.scheduleEditStore.setChangeGatewayModalOpen(true)
                            rootStore.scheduleEditStore.setGatewaySelectionModel([])
                          }}
                  >Change Gateway</Button>
                </Box>
                <Box>
                  <ReviewGatewayTable gateway={rootStore.scheduleEditStore.serverSchedule.gateway}/>
                </Box>
              </Grid>
              {rootStore.scheduleEditStore.serverSchedule.gateway.id !== rootStore.scheduleEditStore.updatedSchedule.gateway.id && (
                <Grid xs={12} lg={5} m={1} ml={2}>
                  <Box display={"flex"} alignItems={"center"}>
                    <Typography variant={"h4"} display={"inline-block"} id={"connected-gateway-title"}>Gateway To
                      Update</Typography>
                    <CustomTooltip message="The new gateway that will process the communication for this schedule" ariaLabelTopic={"Gateway To be used instead"}/>
                  </Box>
                  <Box>
                    <ReviewGatewayTable gateway={rootStore.scheduleEditStore.updatedSchedule.gateway}
                                        idPrefix={"new-"}/>
                  </Box>
                </Grid>
              )}
              <Grid xs={12}></Grid>


            </Grid>

            <Grid xs={12} container spacing={2} pl={2}>
              <Grid xs={12}>
                <Box display={"flex"} alignItems={"center"}>
                  <Typography variant={"h4"} display={"inline-block"} id={"edit-schedule-upcoming-title"}>Upcoming
                    Communications</Typography>
                  <CustomTooltip message="The upcoming timestamps of when a communication will be sent" ariaLabelTopic={"Upcoming communications"}/>
                  <Button variant={"contained"} color={"secondary"} endIcon={<SyncRoundedIcon/>} sx={{ml: 3}}
                          id={"change-schedule"}
                          onClick={() => {
                            rootStore.scheduleEditStore.setChangeScheduleModalOpen(true)
                          }}
                  >Change Schedule</Button>
                </Box>
              </Grid>

              <Grid xs={12} lg={2}>
                <Box width={"200px"}>
                  <Typography>Current Schedule</Typography>
                  <Paper elevation={1}>
                    <List dense id={"current-schedule"}>
                      {rootStore.scheduleEditStore.serverSchedule.nextActionTimes.slice(0, 7).map((communication, index) => (
                        <ListItem key={communication + index.toString()}>
                          <ListItemText primary={communication} sx={{textAlign: "center"}}/>
                        </ListItem>

                      ))}
                    </List>
                  </Paper>
                </Box>
              </Grid>

              {
                rootStore.scheduleEditStore.updateScheduleSpec !== null && (
                  <Grid xs={12} lg={2}>
                    <Box width={"200px"}>
                      <Typography>Proposed Schedule</Typography>
                      <Paper elevation={1}>
                        <UpcomingCommunications scheduleType={rootStore.scheduleEditStore.updateScheduleSpec.scheduleType}
                                                cronString={rootStore.scheduleEditStore.updateScheduleSpec.cronSpec}
                                                intervalSpec={rootStore.scheduleEditStore.updateScheduleSpec.intervalSpec}
                                                calendarWeekSpec={rootStore.scheduleEditStore.updateScheduleSpec.calendarWeekSpec}
                                                calendarMonthSpec={rootStore.scheduleEditStore.updateScheduleSpec.calendarMonthSpec}
                        />
                      </Paper>
                    </Box>
                  </Grid>
                )
              }
            </Grid>
            {
              !rootStore.scheduleEditStore.isUserIdValid() && (
                <Grid xs={12}>
                  <Typography variant={"body1"} sx={{color: theme.palette.error.main}}>Please fix the errors above before
                    trying to modify</Typography>
                </Grid>
              )
            }

            <Grid xs={12} lg={2}>
              <Button
                variant="contained"
                endIcon={<CloseIcon/>}
                id={"cancel-edit-button"}
                color={"info"}
                fullWidth
                sx={{height: 56}}
                onClick={() => {
                  navigate(-1)
                }}
              >
                Cancel
              </Button>
            </Grid>

            {
              rootStore.scheduleEditStore.serverSchedule?.status == ScheduleStatus.Running && (
                <Grid xs={12} lg={2}>
                  <Button
                    variant="contained"
                    endIcon={<PauseCircleRounded/>}
                    id={"pause-schedule-button"}
                    color={"secondary"}
                    fullWidth
                    sx={{height: 56}}
                    onClick={() => {
                      rootStore.scheduleEditStore.setConfirmPauseModalOpen(true)
                    }}
                  >
                    Pause Schedule
                  </Button>
                </Grid>
              )
            }
            {
              rootStore.scheduleEditStore.serverSchedule?.status == ScheduleStatus.Paused && (
                <Grid xs={12} lg={2}>
                  <Button
                    variant="contained"
                    endIcon={<PlayCircleRounded/>}
                    id={"run-schedule-button"}
                    color={"secondary"}
                    fullWidth
                    sx={{height: 56}}
                    onClick={() => {
                      rootStore.scheduleEditStore.setConfirmResumeModalOpen(true)
                    }}
                  >
                    Run Schedule
                  </Button>
                </Grid>
              )
            }

            <Grid xs={12} lg={2}>
              <Button
                variant="contained"
                endIcon={<DeleteRounded/>}
                id={"delete-schedule-button"}
                color={"warning"}
                fullWidth
                sx={{height: 56}}
                onClick={() => {
                  rootStore.scheduleEditStore.setDeleteScheduleModalOpen(true)
                }}
              >
                Delete Schedule
              </Button>
            </Grid>

            <Grid xs={12} lg={2} marginTop={"auto"}>
              <LoadingButton loading={rootStore.scheduleEditStore.loading} variant="contained" fullWidth
                             sx={{height: 56}} disabled={!rootStore.scheduleEditStore.isUserIdValid()}
                             color={"primary"} id={"confirm-edit-button"} endIcon={<SaveRoundedIcon/>}
                             onClick={() => handleModifyModal()}>
                Modify
              </LoadingButton>
            </Grid>

          </>


        )}


      </Grid>
    </>
  )
});

export default EditSchedule;
