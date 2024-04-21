import {observer} from "mobx-react-lite";
import {CircularProgress, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {useStore} from "../../../context/StoreContext.tsx";
import Box from "@mui/material/Box";

const StopCommunicationDescription = observer(() => {
  const rootStore = useStore();
  return (
    <>
      {
        rootStore.historyTableStore.selectedHistoryItem === null && (
          <Box sx={{display: 'flex'}}>
            <CircularProgress/>
          </Box>
        )
      }
      {rootStore.historyTableStore.selectedHistoryItem !== null && (
        <TableContainer component={Paper}>
          <Table sx={{minWidth: 60}} aria-label="communication review table">
            <TableHead>
              <TableRow>
                <TableCell>Field</TableCell>
                <TableCell align="left">Value</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>

              <TableRow>
                <TableCell component="th" scope="row">
                  <strong>Run ID:</strong>
                </TableCell>
                <TableCell
                  align="left"
                  id={"communication-run-id"}>{rootStore.historyTableStore.selectedHistoryItem.id}</TableCell>
              </TableRow>

              <TableRow>
                <TableCell component="th" scope="row">
                  <strong>User ID:</strong>
                </TableCell>
                <TableCell
                  align="left"
                  id={"communication-user-id"}>{rootStore.historyTableStore.selectedHistoryItem.userId}</TableCell>
              </TableRow>


              <TableRow>
                <TableCell component="th" scope="row">
                  <strong>Gateway:</strong>
                </TableCell>
                <TableCell
                  align="left"
                  id={"communication-gateway"}>
                  <span
                    id={"communication-gateway-id"}>{rootStore.historyTableStore.selectedHistoryItem.gatewayId}</span><br/>
                  <span
                    id={"communication-gateway-name"}>{rootStore.historyTableStore.selectedHistoryItem.gatewayName}</span>
                </TableCell>
              </TableRow>


              <TableRow>
                <TableCell component="th" scope="row">
                  <strong>Schedule ID:</strong>
                </TableCell>
                <TableCell
                  align="left"
                  id={"communication-schedule-id"}>
                  {rootStore.historyTableStore.selectedHistoryItem.scheduleId}
                </TableCell>
              </TableRow>


            </TableBody>
          </Table>
        </TableContainer>
      )}

    </>
  )
})

export default StopCommunicationDescription
