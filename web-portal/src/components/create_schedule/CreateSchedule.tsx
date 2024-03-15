import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, InputAdornment, TextField, Typography, useTheme} from "@mui/material";
import {IntervalSpec, ScheduleType} from "../../models/Schedules.ts";
import {observer} from "mobx-react-lite";
import {useStore} from "../../context/StoreContext.tsx";
import Box from "@mui/material/Box";
import CustomTooltip from "../tooltip";
import TimeSelection from "../time_selection";
import {useEffect} from "react";

export interface CreateScheduleProps {
  selectedScheduleType: ScheduleType;
  setSelectedScheduleType: (scheduleType: ScheduleType) => void;
  setIntervalSpec: (intervalSpec: IntervalSpec) => void;
}

const CreateSchedule = observer(({selectedScheduleType, setSelectedScheduleType, setIntervalSpec}: CreateScheduleProps) => {
  const rootStore = useStore();
  const theme = useTheme();

  useEffect(() => {
    setIntervalSpec(rootStore.createScheduleStore.getIntervalObject());
  }, [rootStore.createScheduleStore.intervalOffset, rootStore.createScheduleStore.intervalSeconds, rootStore.createScheduleStore.intervalMinutes, rootStore.createScheduleStore.intervalHours, rootStore.createScheduleStore.intervalDays, rootStore.createScheduleStore.intervalOffsetTime, setIntervalSpec, rootStore.createScheduleStore]);

  return (
    <>
      <Grid xs={12}>
        <Button color={"secondary"} onClick={() => setSelectedScheduleType(ScheduleType.Interval)} sx={{mr: 2}}
                variant={selectedScheduleType == ScheduleType.Interval ? "contained" : "outlined"}>Interval</Button>
        <Button color={"secondary"} onClick={() => setSelectedScheduleType(ScheduleType.CalendarWeek)} sx={{mr: 2}}
                variant={selectedScheduleType == ScheduleType.CalendarWeek ? "contained" : "outlined"}>Days of the
          week</Button>
        <Button color={"secondary"} onClick={() => setSelectedScheduleType(ScheduleType.CalendarMonth)} sx={{mr: 2}}
                variant={selectedScheduleType == ScheduleType.CalendarMonth ? "contained" : "outlined"}>Days of the
          month</Button>
        <Button color={"secondary"} onClick={() => setSelectedScheduleType(ScheduleType.Cron)}
                variant={selectedScheduleType == ScheduleType.Cron ? "contained" : "outlined"}>CRON String</Button>
      </Grid>

      <Grid xs={12}>
        {selectedScheduleType == ScheduleType.Interval && (
          <>
            <Box>
              <Typography variant={"h4"} display={"inline-block"} >Recurring Interval</Typography>
              <CustomTooltip message="Specify the time interval for this communication to run (e.g. Every 12 hours)"/>
              {
                rootStore.createScheduleStore.intervalDays == "0" &&
                rootStore.createScheduleStore.intervalHours == "0" &&
                rootStore.createScheduleStore.intervalMinutes == "0" &&
                rootStore.createScheduleStore.intervalSeconds == "0" ? (
                  <Typography variant={"body1"} color={theme.palette.warning.light}>At least one field must not be 0</Typography>
                ) : <Typography color={theme.palette.success.main}>At least one field must not be 0</Typography>
              }
            </Box>
            <TextField
              id="outlined-start-adornment"
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
              id="outlined-start-adornment"
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
              id="outlined-start-adornment"
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
              id="outlined-start-adornment"
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
                <Typography variant={"h4"} display={"inline-block"} id={"initial-interval-title"}>Offset</Typography>
                <CustomTooltip message="Specify the time to offset when this schedule will run (E.g. every 15 minutes past the hour)"/>
              {
                rootStore.createScheduleStore.isIntervalOffsetValid() ? (
                    <Typography color={theme.palette.success.main}>The offset must be smaller than the total interval time</Typography>
                ) : <Typography variant={"body1"} color={theme.palette.warning.light}>The offset must be smaller than the total interval time</Typography>
              }
              </Box>
              <Box>
                <TextField label="Offset" type="number" variant="outlined" margin={"normal"}
                           value={rootStore.createScheduleStore.intervalOffset} id={"offset-input"}
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
          <p>Days of the week</p>
        )}
        {selectedScheduleType == ScheduleType.CalendarMonth && (
          <p>Days of the month</p>
        )}
        {selectedScheduleType == ScheduleType.Cron && (
          <p>CRON String</p>
        )}
      </Grid>
    </>

  )
})

export default CreateSchedule
