import {observer} from "mobx-react-lite";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, TextField, Typography, useTheme} from "@mui/material";
import ArrowBackRoundedIcon from "@mui/icons-material/ArrowBackRounded";
import {useNavigate} from "react-router-dom";
import {AddScheduleStepper} from "../../components/progress_steps";
import {useStore} from "../../context/StoreContext.tsx";
import Box from "@mui/material/Box";
import CustomTooltip from "../../components/tooltip";
import {CreateSchedule} from "../../components/create_schedule";
import {ReviewGatewayTable} from "../../components/modal/gateway";
import {UpcomingCommunications} from "../../components/upcoming_communications";
import {SelectGatewaySchedule} from "../../components/select_gateway_schedule";
import {useEffect} from "react";

const AddSchedule = observer(() => {
  const navigate = useNavigate();
  const rootStore = useStore();
  const theme = useTheme();

  useEffect(() => {
    rootStore.createScheduleStore.reset();
    rootStore.generateScheduleStore.reset();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12}>
          <Button sx={{mb: 1}} variant="contained" color="primary" onClick={() => navigate("/schedules")}
                  startIcon={<ArrowBackRoundedIcon/>}>Back to schedules</Button>
          <Typography variant="h1" fontSize={"4rem"} id={"preferences-page-heading"}>Add New Schedule</Typography>
        </Grid>
        <AddScheduleStepper>
          {
            rootStore.generateScheduleStore.currentStep == 0 && (
              <>
                <Grid xs={12} md={6}>
                  <Box>
                    <Typography variant={"h4"} display={"inline-block"} id={"add-schedule-user-id-title"}>User ID<span
                      style={{color: theme.palette.error.main}}>*</span></Typography>
                    <CustomTooltip message="The user ID of the user to send the communication to"/>
                  </Box>
                  <Box>
                    <TextField label="UserId" type="text" variant="outlined" margin={"normal"}
                               fullWidth
                               required
                               value={rootStore.generateScheduleStore.userId} id={"user-id-input"}
                               error={!rootStore.generateScheduleStore.isUserIdValid()}
                               helperText={!rootStore.generateScheduleStore.isUserIdValid() ? "Please enter a userId" : ""}
                               onChange={(event) => {
                                 rootStore.generateScheduleStore.setUserId(event.target.value)
                               }
                               }/>
                  </Box>
                </Grid>
              </>
            )
          }

          {
            rootStore.generateScheduleStore.currentStep == 1 && (
              <>
                <SelectGatewaySchedule selectionModel={rootStore.generateScheduleStore.gatewaySelectionModel}
                                       setSelectionModel={rootStore.generateScheduleStore.setGatewaySelectionModel}
                                       setSelectedGateway={rootStore.generateScheduleStore.setSelectedGateway}/>
              </>
            )
          }
          {
            rootStore.generateScheduleStore.currentStep == 2 && (
              <CreateSchedule selectedScheduleType={rootStore.generateScheduleStore.selectedScheduleType}
                              setSelectedScheduleType={rootStore.generateScheduleStore.setSelectedScheduleType}
                              setIntervalSpec={rootStore.generateScheduleStore.setIntervalSpec}
                              setCalendarWeekSpec={rootStore.generateScheduleStore.setCalendarWeekSpec}
                              setCalendarMonthSpec={rootStore.generateScheduleStore.setCalendarMonthSpec}
                              setCronString={rootStore.generateScheduleStore.setCronString}
              />
            )
          }

          {
            rootStore.generateScheduleStore.currentStep == 3 && (
              <>
                <Grid xs={12}>
                  <Typography variant={"h4"} display={"inline-block"} id={"review-title"}>Review Details:</Typography>
                </Grid>
                <Grid xs={12}>
                  <Typography variant={"h5"} display={"inline-block"} id={"user-id"}><strong>User
                    ID:</strong> {rootStore.generateScheduleStore.userId}</Typography>
                </Grid>
                <Grid xs={12} md={7} lg={5} xl={4}>
                  <Typography variant={"h5"} display={"inline-block"} id={"gateway-title"}
                              mb={1}><strong>Gateway:</strong></Typography>
                  <ReviewGatewayTable gateway={rootStore.generateScheduleStore.selectedGateway}></ReviewGatewayTable>
                </Grid>
                <Grid xs={12}>
                  <Typography variant={"h5"} display={"inline-block"} id={"user-id"} mb={1}><strong>Upcoming
                    Communications:</strong></Typography>
                  <UpcomingCommunications scheduleType={rootStore.generateScheduleStore.selectedScheduleType}
                                          intervalSpec={rootStore.generateScheduleStore.intervalSpec}
                                          calendarWeekSpec={rootStore.generateScheduleStore.calendarWeekSpec}
                                          calendarMonthSpec={rootStore.generateScheduleStore.calendarMonthSpec}
                                          cronString={rootStore.generateScheduleStore.cronString}/>
                </Grid>

              </>


            )
          }
        </AddScheduleStepper>
      </Grid>
    </>
  )
});

export default AddSchedule;
