import {observer} from "mobx-react-lite";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from "@mui/material";
import {useStore} from "../../../context/StoreContext.tsx";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import List from "@mui/material/List";
import {UpcomingCommunicationsList} from "../../upcoming_communications";

const ScheduleChangesTable = observer(() => {
  const rootStore = useStore();

  return (
    <>
      <TableContainer component={Paper}>
        <Table sx={{minWidth: 150}} aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell>Option</TableCell>
              <TableCell align="left">Old Value</TableCell>
              <TableCell align="left">New Value</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rootStore.scheduleEditStore.hasUserIdChanged() && (
              <TableRow>
                <TableCell component="th" scope="row" id={"user-id-confirm-table-cell"}>
                  User Id
                </TableCell>
                <TableCell id={"user-id-old-value"}
                           align="left">{rootStore.scheduleEditStore.serverSchedule?.userId}
                </TableCell>
                <TableCell id={"user-id-new-value"}
                           align="left">{rootStore.scheduleEditStore.updatedSchedule?.userId}</TableCell>
              </TableRow>
            )}
            {rootStore.scheduleEditStore.hasGatewayChanged() && (
              <TableRow>
                <TableCell component="th" scope="row" id={"gateway-confirm-table-cell"}>
                  Gateway
                </TableCell>
                <TableCell
                  align="left">{
                  <>
                    <Typography
                      variant={"body1"}
                      id={"old-gateway-id"}>ID: {rootStore.scheduleEditStore.serverSchedule?.gateway.id}</Typography>
                    <Typography variant={"body1"} id={"old-gateway-friendly-name"}>Friendly
                      Name: {rootStore.scheduleEditStore.serverSchedule?.gateway.friendlyName}</Typography>
                  </>
                }</TableCell>
                <TableCell id={"gateway-new-value"}
                           align="left">{
                  <>
                    <Typography
                      variant={"body1"}
                      id={"new-gateway-id"}>ID: {rootStore.scheduleEditStore.updatedSchedule?.gateway.id}</Typography>
                    <Typography variant={"body1"} id={"new-gateway-friendly-name"}>Friendly
                      Name: {rootStore.scheduleEditStore.updatedSchedule?.gateway.friendlyName}</Typography>
                  </>
                }</TableCell>
              </TableRow>
            )}
            {rootStore.scheduleEditStore.hasScheduleChanged() && (
              <TableRow>
                <TableCell component="th" scope="row" id={"schedule-confirm-table-cell"}>
                  Schedule
                </TableCell>
                <TableCell
                  align="left">
                  <List dense id={"schedule-old-value"}>
                    {rootStore.scheduleEditStore.serverSchedule?.nextActionTimes.slice(0, 7).map((communication, index) => (
                      <ListItem key={communication + index.toString()} sx={{pl: 0}}>
                        <ListItemText primary={communication} sx={{textAlign: "left"}}/>
                      </ListItem>

                    ))}
                  </List></TableCell>
                <TableCell
                  align="left">
                  <List dense id={"schedule-new-value"}>
                    <UpcomingCommunicationsList/>
                  </List></TableCell>
              </TableRow>
            )}


          </TableBody>
        </Table>
      </TableContainer>


    </>
  )
});

export default ScheduleChangesTable;
