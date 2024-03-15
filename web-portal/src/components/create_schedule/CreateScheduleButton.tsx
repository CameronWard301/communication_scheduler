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
      userId: rootStore.scheduleAddStore.userId,
      gatewayId: rootStore.scheduleAddStore.gatewaySelectionModel[0] as string,
      scheduleType: rootStore.scheduleAddStore.selectedScheduleType,
      intervalSpec: rootStore.scheduleAddStore.intervalSpec
    }
    createSchedule(scheduleRequest)
  }

  return (
    <LoadingButton sx={{ml: 2}} variant={"contained"} onClick={() => handleCreateSchedule()} loading={rootStore.scheduleAddStore.loading} endIcon={<ScheduleSendRoundIcon/>}>Create Schedule</LoadingButton>
  )
})

export default CreateScheduleButton;
