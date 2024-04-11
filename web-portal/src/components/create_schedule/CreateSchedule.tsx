import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Button,
  InputAdornment,
  TextField,
  Typography,
  useTheme
} from "@mui/material";
import {
  CalendarMonthSpec,
  CalendarWeekSpec,
  DayOfWeek,
  IntervalSpec,
  Month,
  ScheduleType
} from "../../models/Schedules.ts";
import {observer} from "mobx-react-lite";
import {useStore} from "../../context/StoreContext.tsx";
import Box from "@mui/material/Box";
import CustomTooltip from "../tooltip";
import TimeSelection from "../time_selection";
import {useEffect} from "react";
import ScheduleToggleButton from "../schedule_toggle_button";
import {TimePicker} from '@mui/x-date-pickers/TimePicker';
import {ExpandMoreRounded} from "@mui/icons-material";
import parser from "cron-parser";

export interface CreateScheduleProps {
  selectedScheduleType: ScheduleType;
  setSelectedScheduleType: (scheduleType: ScheduleType) => void;
  setIntervalSpec: (intervalSpec: IntervalSpec) => void;
  setCalendarWeekSpec: (calendarWeekSpec: CalendarWeekSpec) => void;
  setCalendarMonthSpec: (calendarMonthSpec: CalendarMonthSpec) => void;
  setCronString: (cronString: string) => void;
}

const CreateSchedule = observer(({
                                   selectedScheduleType,
                                   setSelectedScheduleType,
                                   setIntervalSpec,
                                   setCalendarWeekSpec,
                                   setCalendarMonthSpec,
                                   setCronString
                                 }: CreateScheduleProps) => {
  const rootStore = useStore();
  const theme = useTheme();

  const topDayOfWeekRow = [DayOfWeek.EveryDay, DayOfWeek.Weekdays, DayOfWeek.Weekend];
  const bottomDayOfWeekRow = [DayOfWeek.Monday, DayOfWeek.Tuesday, DayOfWeek.Wednesday, DayOfWeek.Thursday, DayOfWeek.Friday, DayOfWeek.Saturday, DayOfWeek.Sunday];
  const months = [Month.January, Month.February, Month.March, Month.April, Month.May, Month.June, Month.July, Month.August, Month.September, Month.October, Month.November, Month.December];
  useEffect(() => {
    setIntervalSpec(rootStore.createScheduleStore.getIntervalObject());
  }, [rootStore.createScheduleStore.intervalOffset, rootStore.createScheduleStore.intervalSeconds, rootStore.createScheduleStore.intervalMinutes, rootStore.createScheduleStore.intervalHours, rootStore.createScheduleStore.intervalDays, rootStore.createScheduleStore.intervalOffsetTime, setIntervalSpec, rootStore.createScheduleStore]);

  useEffect(() => {
    setCalendarWeekSpec(rootStore.createScheduleStore.getCalendarWeekObject());
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.createScheduleStore.dayOfWeekType, rootStore.createScheduleStore.scheduleTime, rootStore.createScheduleStore]);

  useEffect(() => {
    setCalendarMonthSpec(rootStore.createScheduleStore.getCalendarMonthObject());
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.createScheduleStore.monthType, rootStore.createScheduleStore.scheduleTime, rootStore.createScheduleStore.dayOfMonth, rootStore.createScheduleStore]);

  useEffect(() => {
    setCronString(rootStore.createScheduleStore.cronString);
    const testCronParses = () => {
      try {
        parser.parseExpression(rootStore.createScheduleStore.cronString);
        return true;
      } catch (e) {
        return false;
      }
    }
    rootStore.createScheduleStore.setCronParseError(!testCronParses());
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.createScheduleStore.cronString]);


  return (
    <>
      <Grid xs={12}>
        <Button color={"secondary"} onClick={() => setSelectedScheduleType(ScheduleType.Interval)} sx={{mr: 2, mb: 2}}
                variant={selectedScheduleType == ScheduleType.Interval ? "contained" : "outlined"}
                id={"schedule-interval"}>Interval</Button>
        <Button color={"secondary"} onClick={() => setSelectedScheduleType(ScheduleType.CalendarWeek)}
                sx={{mr: 2, mb: 2}}
                variant={selectedScheduleType == ScheduleType.CalendarWeek ? "contained" : "outlined"}
                id={"calendar-week"}>Days of the
          week</Button>
        <Button color={"secondary"} onClick={() => setSelectedScheduleType(ScheduleType.CalendarMonth)}
                sx={{mr: 2, mb: 2}}
                variant={selectedScheduleType == ScheduleType.CalendarMonth ? "contained" : "outlined"}
                id={"calendar-month"}>Days of the
          month</Button>
        <Button color={"secondary"} onClick={() => setSelectedScheduleType(ScheduleType.Cron)} sx={{mr: 2, mb: 2}}
                variant={selectedScheduleType == ScheduleType.Cron ? "contained" : "outlined"} id={"cron-string"}>CRON
          String</Button>
      </Grid>

      <Grid xs={12}>
        {selectedScheduleType == ScheduleType.Interval && (
          <>
            <Box sx={{mb: 1}}>
              <Typography variant={"h4"} display={"inline-block"} id={"create-schedule-recurring-interval-title"}>Recurring
                Interval</Typography>
              <CustomTooltip message="Specify the time interval for this communication to run (e.g. Every 12 hours)"/>
              {
                rootStore.createScheduleStore.intervalDays == "0" &&
                rootStore.createScheduleStore.intervalHours == "0" &&
                rootStore.createScheduleStore.intervalMinutes == "0" &&
                rootStore.createScheduleStore.intervalSeconds == "0" ? (
                  <Typography variant={"body1"} color={theme.palette.warning.light}>At least one field must not be
                    0</Typography>
                ) : <Typography
                  color={rootStore.createScheduleStore.isIntervalFieldsValid() ? theme.palette.success.main : theme.palette.error.main}>At
                  least one field must not be 0</Typography>
              }
            </Box>
            <TextField
              id="days-interval-input"
              type={"number"}
              sx={{mr: 2, width: "150px"}}
              InputProps={{
                endAdornment: <InputAdornment position="end">Days</InputAdornment>,
              }}
              error={rootStore.createScheduleStore.intervalDays == ""}
              helperText={rootStore.createScheduleStore.intervalDays == "" ? "Please enter a value or 0" : ""}
              value={rootStore.createScheduleStore.intervalDays}
              onChange={(e) => rootStore.createScheduleStore.setIntervalDays(e.target.value)}
            />

            <TextField
              id="hours-interval-input"
              type={"number"}
              sx={{mr: 2, width: "150px"}}
              InputProps={{
                endAdornment: <InputAdornment position="end">Hours</InputAdornment>,
              }}
              error={rootStore.createScheduleStore.intervalHours == ""}
              helperText={rootStore.createScheduleStore.intervalHours == "" ? "Please enter a value or 0" : ""}
              value={rootStore.createScheduleStore.intervalHours}
              onChange={(e) => rootStore.createScheduleStore.setIntervalHours(e.target.value)}
            />

            <TextField
              id="minutes-interval-input"
              type={"number"}
              sx={{mr: 2, width: "150px"}}
              InputProps={{
                endAdornment: <InputAdornment position="end">Minutes</InputAdornment>,
              }}
              error={rootStore.createScheduleStore.intervalMinutes == ""}
              helperText={rootStore.createScheduleStore.intervalMinutes == "" ? "Please enter a value or 0" : ""}
              value={rootStore.createScheduleStore.intervalMinutes}
              onChange={(e) => rootStore.createScheduleStore.setIntervalMinutes(e.target.value)}
            />
            <TextField
              id="seconds-interval-input"
              type={"number"}
              sx={{mr: 2, width: "150px"}}
              InputProps={{
                endAdornment: <InputAdornment position="end">Seconds</InputAdornment>,
              }}
              error={rootStore.createScheduleStore.intervalSeconds == ""}
              helperText={rootStore.createScheduleStore.intervalSeconds == "" ? "Please enter a value or 0" : ""}
              value={rootStore.createScheduleStore.intervalSeconds}
              onChange={(e) => rootStore.createScheduleStore.setIntervalSeconds(e.target.value)}
            />

            <Box mt={3}>
              <Box>
                <Typography variant={"h4"} display={"inline-block"}
                            id={"create-schedule-offset-title"}>Offset</Typography>
                <CustomTooltip
                  message="Specify the time to offset when this schedule will run (E.g. every 15 minutes past the hour)"/>
                {
                  rootStore.createScheduleStore.isIntervalOffsetValid() ? (
                    <Typography color={theme.palette.success.main}>The offset must be smaller than the total interval
                      time</Typography>
                  ) : <Typography variant={"body1"} color={theme.palette.warning.light}>The offset must be smaller than
                    the total interval time</Typography>
                }
              </Box>
              <Box>
                <TextField label="Offset" type="number" variant="outlined" margin={"normal"}
                           value={rootStore.createScheduleStore.intervalOffset} id={"offset-interval-input"}
                           error={rootStore.createScheduleStore.intervalOffset == ""}
                           helperText={rootStore.createScheduleStore.intervalOffset == "" ? "Please enter a value or 0" : ""}
                           onChange={(event) => rootStore.createScheduleStore.setIntervalOffset(event.target.value)}/>
                <TimeSelection keyId={"offset-time"}
                               value={rootStore.createScheduleStore.intervalOffsetTime} onChange={(event) => {
                  rootStore.createScheduleStore.setIntervalOffsetTime(event.target.value)
                }}/>
              </Box>
            </Box>
          </>


        )}
        {selectedScheduleType == ScheduleType.CalendarWeek && (
          <>
            <Box>
              <Typography variant={"h4"} display={"inline-block"} id={"calendar-week-title"}>Recurring
                Day(s)</Typography>
              <CustomTooltip message="Select the day(s) of the week you want the communication to be sent on"/>
              <Grid container spacing={2}>
                {
                  topDayOfWeekRow.map((day) => {
                    return (
                      <Grid key={day}>
                        <ScheduleToggleButton scheduleType={day}
                                              setType={(type) => rootStore.createScheduleStore.setDayOfWeekType(type as DayOfWeek)}
                                              selectedScheduleType={rootStore.createScheduleStore.dayOfWeekType}/>
                      </Grid>
                    )
                  })
                }
                <Grid xs={12}></Grid>
                {
                  bottomDayOfWeekRow.map((day) => {
                    return (
                      <Grid key={day}>
                        <ScheduleToggleButton scheduleType={day}
                                              setType={(type) => rootStore.createScheduleStore.setDayOfWeekType(type as DayOfWeek)}
                                              selectedScheduleType={rootStore.createScheduleStore.dayOfWeekType}/>
                      </Grid>
                    )
                  })
                }
                <Grid xs={12}></Grid>


                <Grid xs={12}>
                  <Typography variant={"h4"} display={"inline-block"} mt={4}>Time</Typography>
                  <CustomTooltip
                    message="Enter the time you would like the communication to be sent in 12-hour format. By default, the communication will be set to run at 12am UTC. "/>

                </Grid>
                <Grid xs={12}>
                  <TimePicker
                    name={"schedule-time"}
                    key={"schedule-time"}
                    views={['hours', 'minutes']}
                    onError={() => {
                      rootStore.createScheduleStore.setScheduleTimeError(true)
                    }}
                    value={rootStore.createScheduleStore.scheduleTime}
                    onAccept={() => {
                      rootStore.createScheduleStore.setScheduleTimeError(false)
                    }}
                    onChange={(newValue) => rootStore.createScheduleStore.setScheduleTime(newValue)}
                  />
                  {
                    rootStore.createScheduleStore.scheduleTimeError && (
                      <Typography variant={"body1"} color={theme.palette.error.main}>Please enter a valid time to
                        continue</Typography>
                    )
                  }
                </Grid>
              </Grid>


            </Box>
          </>
        )}
        {selectedScheduleType == ScheduleType.CalendarMonth && (
          <>
            <Grid container spacing={2}>
              <Grid xs={12}>
                <Typography variant={"h4"} display={"inline-block"} id={"calendar-month-title"}>Recurring
                  Month(s)<span
                    style={{color: theme.palette.error.main}}>*</span></Typography>
                <CustomTooltip
                  message="Enter the day of the month you want the communication to be sent on. Note that if the date doesn't exist e.g the 31st of September, then the communication will never be sent"/>
              </Grid>

              <Grid xs={12}>
                <TextField inputMode={"numeric"} type={"number"}
                           id={"day-of-the-month"}
                           label={"Day of the month"}
                           variant={"outlined"}
                           sx={{width: "345px"}}
                           value={rootStore.createScheduleStore.dayOfMonth}
                           error={!rootStore.createScheduleStore.isDayOfMonthValid()}
                           helperText={!rootStore.createScheduleStore.isDayOfMonthValid() ? "Please enter a valid day of the month between 1 and 31" : ""}
                           onChange={(e) => rootStore.createScheduleStore.setDayOfMonth(e.target.value)}
                />

                <Grid xs={12} mt={4}>
                  <Typography variant={"h4"} display={"inline-block"}>Select Month(s)</Typography>
                  <CustomTooltip
                    message="Enter the day of the month you want the communication to be sent on. Note that if the date doesn't exist e.g the 31st of September, then the communication will never be sent"/>

                  <Grid mb={2}>
                    <ScheduleToggleButton scheduleType={Month.EveryMonth}
                                          setType={(type) => rootStore.createScheduleStore.setMonthType(type as Month)}
                                          selectedScheduleType={rootStore.createScheduleStore.monthType}/>
                  </Grid>

                </Grid>
              </Grid>

              <Grid xl={8} lg={9} pb={0}>
                <Grid container>


                  {
                    months.map((month) => {
                      return (
                        <Grid mb={2} xs={4} key={month}>
                          <ScheduleToggleButton scheduleType={month}
                                                setType={(type) => rootStore.createScheduleStore.setMonthType(type as Month)}
                                                selectedScheduleType={rootStore.createScheduleStore.monthType}/>

                        </Grid>
                      )
                    })
                  }
                </Grid>
              </Grid>
              <Grid xs={12} pt={0} pb={2}>
                <Typography visibility={rootStore.createScheduleStore.isMonthTypeValid() ? "hidden" : "visible"}
                            color={theme.palette.error.main}>Your selected date does not exist</Typography>

              </Grid>


              <Grid xs={12} py={0}>
                <Typography variant={"h4"} display={"inline-block"}>Time</Typography>
                <CustomTooltip
                  message="Enter the time you would like the communication to be sent in 12-hour format. By default, the communication will be set to run at 12am UTC. "/>

              </Grid>
              <Grid py={0}>
                <TimePicker
                  name={"schedule-time"}
                  key={"schedule-time"}
                  views={['hours', 'minutes']}
                  onError={() => {
                    rootStore.createScheduleStore.setScheduleTimeError(true)
                  }}
                  value={rootStore.createScheduleStore.scheduleTime}
                  onAccept={() => {
                    rootStore.createScheduleStore.setScheduleTimeError(false)
                  }}
                  onChange={(newValue) => rootStore.createScheduleStore.setScheduleTime(newValue)}
                />
                {
                  rootStore.createScheduleStore.scheduleTimeError && (
                    <Typography variant={"body1"} color={theme.palette.error.main}>Please enter a valid time to
                      continue</Typography>
                  )
                }
              </Grid>
            </Grid>

          </>
        )
        }
        {
          selectedScheduleType == ScheduleType.Cron && (
            <>
              <Grid container spacing={2}>
                <Grid xs={12}>
                  <Typography variant={"h4"} display={"inline-block"} id={"cron-string-title"}>Cron String
                    (Advanced)</Typography>
                  <CustomTooltip
                    message="Enter a CRON string that specifies when to send the communication"/>
                </Grid>
                <Grid xs={12} lg={6}>
                  <Accordion>
                    <AccordionSummary
                      expandIcon={<ExpandMoreRounded/>}
                      aria-controls="extra-help"
                      id="extra-help"
                    >
                      Extra Help
                    </AccordionSummary>
                    <AccordionDetails>
                      <Typography variant={"h6"}>Format:</Typography>
                      <Typography fontFamily={"monospace"}>
                        ┌───────────── Minute (0 - 59)<br/>
                        │ ┌───────────── Hour (0 - 23) <br/>
                        │ │ ┌───────────── Day of the month (1 - 31)<br/>
                        │ │ │ ┌───────────── Month (1 - 12)<br/>
                        │ │ │ │ ┌───────────── Day of the week (0 - 6) (Sunday to Saturday)<br/>
                        │ │ │ │ │<br/>
                        │ │ │ │ │<br/>
                        * * * * *
                      </Typography>
                      <Typography variant={"h6"}>Examples:</Typography>
                      <Typography fontFamily={"monospace"} fontWeight={"lighter"} id={"help-examples"}>
                        * * * * * - Every minute<br/>
                        0 * * * * - Every hour<br/>
                        0 8-10 * * * - Every 8, 9 and 10 AM each day<br/>
                        0 0 * * 0 - Every Sunday at midnight<br/>
                        0 0 1 1 * - Every January 1st at midnight<br/>
                      </Typography>
                    </AccordionDetails>
                  </Accordion>


                </Grid>

                <Grid xs={12}>
                  <TextField inputMode={"text"} type={"text"}
                             label={"Cron string"}
                             variant={"outlined"}
                             margin={"dense"}
                             id={"cron-string-input"}
                             placeholder={"* * * * *"}
                             sx={{width: "345px"}}
                             value={rootStore.createScheduleStore.cronString}
                             error={!rootStore.createScheduleStore.isCronStringValid() || rootStore.createScheduleStore.cronParseError}
                             helperText={!rootStore.createScheduleStore.isCronStringValid() || rootStore.createScheduleStore.cronParseError ? "Please enter a valid cron string, check the help section for more details above" : ""}
                             onChange={(e) => rootStore.createScheduleStore.setCronString(e.target.value)}
                  />
                </Grid>
              </Grid>
            </>
          )
        }
      </Grid>
    </>

  )
})

export default CreateSchedule
