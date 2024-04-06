import {Button} from "@mui/material";
import {DayOfWeek, Month} from "../../models/Schedules.ts";

export interface ScheduleToggleButtonProps<T> {
  setType: (type: T) => void;
  scheduleType: T;
  selectedScheduleType: T;

}

const ScheduleToggleButton = ({
                                setType,
                                scheduleType,
                                selectedScheduleType
                              }: ScheduleToggleButtonProps<DayOfWeek | Month>) => {
  return (
    <Button color={"info"} onClick={() => setType(scheduleType)} sx={{mr: 2, width: "150px"}}
            id={`schedule-type-${scheduleType}`}
            variant={scheduleType == selectedScheduleType ? "contained" : "outlined"}>{scheduleType}</Button>

  )
}

export default ScheduleToggleButton;
