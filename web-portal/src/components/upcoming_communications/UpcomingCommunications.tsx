import {observer} from "mobx-react-lite";
import { Paper, Typography} from "@mui/material";
import {IntervalSpec, ScheduleType} from "../../models/Schedules.ts";
import {useEffect, useState} from "react";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import Box from "@mui/material/Box";

export interface UpcomingCommunicationsProps {
  scheduleType: ScheduleType;
  intervalSpec?: IntervalSpec;
}




const UpcomingCommunications = observer(({scheduleType, intervalSpec}: UpcomingCommunicationsProps) => {
  const [upcomingCommunications, setUpcomingCommunications] = useState<string[]>([])
  const numberOfCommunications = 5;

  const calculateIntervalInSecondsFromIntervalSpec = (intervalSpec: IntervalSpec) : number => {
    const interval = parseInt(intervalSpec.seconds) + (parseInt(intervalSpec.minutes) * 60) + (parseInt(intervalSpec.hours) * 60 * 60) + (parseInt(intervalSpec.days) * 60 * 60 * 24);

    if (parseInt(intervalSpec.offset) > 0) {
      switch (intervalSpec.offsetPeriod) {
        case "S":
          return interval + parseInt(intervalSpec.offset);
        case "M":
          return interval + parseInt(intervalSpec.offset) * 60;
        case "H":
          return interval + parseInt(intervalSpec.offset) * 60 * 60;
        case "D":
          return interval + parseInt(intervalSpec.offset) * 60 * 60 * 24;
      }
    }
    return interval;

  }

  const getUpcomingTimestamps = (count: number, intervalInSeconds: number) => {
    const timestamps: string[] = [];
    const currentDate = new Date();
    for (let i = 0; i < count; i++) {
      timestamps.push(new Date(currentDate.getTime() + intervalInSeconds * 1000 * (i + 1)).toLocaleString());
    }
    return timestamps;
  }

  useEffect(() => {
    //calculate upcoming communications
    switch (scheduleType) {
      case ScheduleType.Interval:
        setUpcomingCommunications(getUpcomingTimestamps(numberOfCommunications, calculateIntervalInSecondsFromIntervalSpec(intervalSpec!)))
        break;
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [scheduleType, intervalSpec]);

  return (
    <>
      <Typography variant={"h5"} display={"inline-block"} id={"user-id"} mb={1}><strong>Upcoming Communications:</strong></Typography>

      <Box width={"200px"}>
        <Paper elevation={1}>
          <List dense>
            {upcomingCommunications.map((communication, index) => (
              <ListItem key={communication+index.toString()}>
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
