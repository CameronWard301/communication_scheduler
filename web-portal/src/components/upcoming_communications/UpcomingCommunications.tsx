import {observer} from "mobx-react-lite";
import {Paper} from "@mui/material";
import {CalendarMonthSpec, CalendarWeekSpec, IntervalSpec, ScheduleType,} from "../../models/Schedules.ts";
import {useEffect, useState} from "react";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import Box from "@mui/material/Box";
import {useUpcomingCommunications} from "./UseUpcomingCommunications.tsx";

export interface UpcomingCommunicationsProps {
  scheduleType: ScheduleType;
  intervalSpec?: IntervalSpec;
  calendarWeekSpec?: CalendarWeekSpec;
  calendarMonthSpec?: CalendarMonthSpec;
  cronString?: string;

}


const UpcomingCommunications = observer(({
                                           scheduleType,
                                           intervalSpec,
                                           calendarWeekSpec,
                                           calendarMonthSpec,
                                           cronString
                                         }: UpcomingCommunicationsProps) => {
  const [upcomingCommunications, setUpcomingCommunications] = useState<string[]>([])
  const {
    getUpcomingTimestampsForInterval,
    calculateIntervalFromMonthSpec,
    calculateIntervalsFromCronString,
    calculateIntervalFromDayOFWeekSpec
  } = useUpcomingCommunications();

  useEffect(() => {
    switch (scheduleType) {
      case ScheduleType.Interval:
        setUpcomingCommunications(getUpcomingTimestampsForInterval(intervalSpec!))
        break;
      case ScheduleType.CalendarWeek:
        setUpcomingCommunications(calculateIntervalFromDayOFWeekSpec(calendarWeekSpec!))
        break;
      case ScheduleType.CalendarMonth:
        setUpcomingCommunications(calculateIntervalFromMonthSpec(calendarMonthSpec!))
        break;
      case ScheduleType.Cron:
        setUpcomingCommunications(calculateIntervalsFromCronString(cronString!))
        break
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [scheduleType, intervalSpec, calendarWeekSpec, calendarMonthSpec]);

  return (
    <>
      <Box width={"200px"}>
        <Paper elevation={1}>
          <List dense id={"upcoming-communication-list"}>
            {upcomingCommunications.map((communication, index) => (
              <ListItem key={communication + index.toString()}>
                <ListItemText primary={communication} sx={{textAlign: "center"}}/>
              </ListItem>

            ))}
          </List>
        </Paper>
      </Box>
    </>

  )
})

export default UpcomingCommunications;
