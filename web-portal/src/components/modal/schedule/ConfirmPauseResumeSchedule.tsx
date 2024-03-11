import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {useEffect} from "react";
import {useStore} from "../../../context/StoreContext.tsx";
import {useScheduleService} from "../../../service/ScheduleService.ts";
import {observer} from "mobx-react-lite";

export interface ConfirmPauseResumeScheduleProps {
  scheduleId: string;
}

const ConfirmPauseResumeSchedule = observer(({scheduleId} : ConfirmPauseResumeScheduleProps) => {
  const rootStore = useStore();
  const {getScheduleById} = useScheduleService();

  useEffect(() => {
    getScheduleById(scheduleId);
  }, []);

  return (
    <TableContainer component={Paper}>
      <Table sx={{minWidth: 60}} aria-label="gateway review table">
        <TableHead>
          <TableRow>
            <TableCell>Field</TableCell>
            <TableCell align="left">Value</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>

          {
            rootStore.scheduleEditStore.serverSchedule.id !== "0" && (
              <TableRow>
                <TableCell component="th" scope="row">
                  <strong>ID:</strong>
                </TableCell>
                <TableCell
                  align="left" id={"schedule-id"}>{rootStore.scheduleEditStore.serverSchedule.id}</TableCell>
              </TableRow>
            )
          }

          <TableRow>
            <TableCell component="th" scope="row">
              <strong>Gateway Name:</strong>
            </TableCell>
            <TableCell
              align="left" id={"gateway-friendly-name"}>{rootStore.scheduleEditStore.serverSchedule.gatewayName}</TableCell>
          </TableRow>

          <TableRow>
            <TableCell component="th" scope="row">
              <strong>User ID:</strong>
            </TableCell>
            <TableCell
              align="left" id={"user-id"}>{rootStore.scheduleEditStore.serverSchedule.userId}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell component="th" scope="row">
              <strong>Schedule:</strong>
            </TableCell>
            <TableCell
              align="left" id={"schedule"}>{rootStore.scheduleEditStore.serverSchedule.nextActionTimes.slice(0,3).map((nextActionTime) => (
              <div key={nextActionTime}>
                {nextActionTime}
              </div>
            ))}</TableCell>
          </TableRow>



        </TableBody>
      </Table>
    </TableContainer>
  )
});

export default ConfirmPauseResumeSchedule;
