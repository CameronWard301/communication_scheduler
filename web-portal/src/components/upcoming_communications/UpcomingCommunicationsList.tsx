import {useEffect, useState} from "react";
import {useUpcomingCommunications} from "./UseUpcomingCommunications.tsx";
import {ScheduleType} from "../../models/Schedules.ts";
import {observer} from "mobx-react-lite";
import {useStore} from "../../context/StoreContext.tsx";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";

const UpcomingCommunicationsList = observer(() => {
  const rootStore = useStore();
  const [upcomingCommunications, setUpcomingCommunications] = useState<string[]>([])
  const {
    getUpcomingTimestampsForInterval,
    calculateIntervalFromMonthSpec,
    calculateIntervalsFromCronString,
    calculateIntervalFromDayOFWeekSpec
  } = useUpcomingCommunications();

  useEffect(() => {
    if (!rootStore.scheduleEditStore.hasScheduleChanged() || !rootStore.scheduleEditStore.updateScheduleSpec) {
      return
    }
    switch (rootStore.scheduleEditStore.updateScheduleSpec.scheduleType) {
      case ScheduleType.Interval:
        setUpcomingCommunications(getUpcomingTimestampsForInterval(rootStore.scheduleEditStore.updateScheduleSpec.intervalSpec))
        break;
      case ScheduleType.CalendarWeek:
        setUpcomingCommunications(calculateIntervalFromDayOFWeekSpec(rootStore.scheduleEditStore.updateScheduleSpec.calendarWeekSpec))
        break;
      case ScheduleType.CalendarMonth:
        setUpcomingCommunications(calculateIntervalFromMonthSpec(rootStore.scheduleEditStore.updateScheduleSpec.calendarMonthSpec))
        break;
      case ScheduleType.Cron:
        setUpcomingCommunications(calculateIntervalsFromCronString(rootStore.scheduleEditStore.updateScheduleSpec.cronSpec))
        break
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.scheduleEditStore.updateScheduleSpec!.scheduleType, rootStore.scheduleEditStore.updateScheduleSpec!.intervalSpec, rootStore.scheduleEditStore.updateScheduleSpec!.calendarWeekSpec, rootStore.scheduleEditStore.updateScheduleSpec!.calendarMonthSpec, rootStore.scheduleEditStore.updateScheduleSpec!.cronSpec]);

  return (
    upcomingCommunications.map((communication, index) => (
      <ListItem key={communication + index.toString()} sx={{pl: 0}}>
        <ListItemText primary={communication} sx={{textAlign: "left"}}/>
      </ListItem>

    ))
  )

})

export default UpcomingCommunicationsList;
