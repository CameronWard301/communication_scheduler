import LoadingButton from "@mui/lab/LoadingButton";
import ScheduleSendRoundIcon from "@mui/icons-material/ScheduleSend";
import {observer} from "mobx-react-lite";
import {useStore} from "../../context/StoreContext.tsx";
import {useScheduleService} from "../../service/ScheduleService.ts";
import {CreateScheduleRequest} from "../../models/Schedules.ts";

const CreateScheduleButton = observer(() => {
  const rootStore = useStore();
  const {createSchedule} = useScheduleService()

  const handleCreateSchedule = () => {
    const scheduleRequest: CreateScheduleRequest = {
      userId: rootStore.generateScheduleStore.userId,
      gatewayId: rootStore.generateScheduleStore.gatewaySelectionModel[0] as string,
      scheduleType: rootStore.generateScheduleStore.selectedScheduleType,
      intervalSpec: rootStore.generateScheduleStore.intervalSpec,
      calendarWeekSpec: rootStore.generateScheduleStore.calendarWeekSpec,
      calendarMonthSpec: rootStore.generateScheduleStore.calendarMonthSpec,
      cronSpec: rootStore.generateScheduleStore.cronString
    }
    createSchedule(scheduleRequest)
  }

  return (
    <LoadingButton sx={{ml: 2}} variant={"contained"} onClick={() => handleCreateSchedule()}
                   id={"create-schedule-button"}
                   loading={rootStore.generateScheduleStore.loading} endIcon={<ScheduleSendRoundIcon/>}>Create
      Schedule</LoadingButton>
  )
})

export default CreateScheduleButton;
