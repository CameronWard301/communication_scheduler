import {observer} from "mobx-react-lite";
import Box from "@mui/material/Box";
import {
  Button,
  Chip,
  CircularProgress,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  useTheme
} from "@mui/material";
import {useStore} from "../../../context/StoreContext.tsx";
import {getStatusColour} from "../../../models/History.ts";
import LoadingButton from "@mui/lab/LoadingButton";
import StopCircleRoundedIcon from "@mui/icons-material/StopCircleRounded";
import StorageRoundedIcon from "@mui/icons-material/StorageRounded";
import ScheduleSendRoundIcon from "@mui/icons-material/ScheduleSend";

const InspectHistoryModalDescription = observer(() => {
  const rootStore = useStore();
  const theme = useTheme();

  return (
    <>{
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
                <TableCell align="left">Action</TableCell>
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
                <TableCell></TableCell>
              </TableRow>

              <TableRow>
                <TableCell component="th" scope="row">
                  <strong>Status:</strong>
                </TableCell>
                <TableCell
                  align="left"
                  id={"communication-status"}
                >
                  <Chip sx={{
                    backgroundColor: getStatusColour(rootStore.historyTableStore.selectedHistoryItem.status, theme),
                    width: "auto"
                  }} label={rootStore.historyTableStore.selectedHistoryItem.status}/>
                </TableCell>
                <TableCell>
                  {rootStore.historyTableStore.selectedHistoryItem.status === "Running" && (
                    <LoadingButton id={"stop-inspect-communication"}
                                   loading={rootStore.historyTableStore.isLoading}
                                   variant={"contained"}
                                   endIcon={<StopCircleRoundedIcon/>}
                                   sx={{width: "220px"}}
                                   onClick={() => rootStore.historyTableStore.setOpenConfirmStopModal(true)}
                    >
                      Stop Communication
                    </LoadingButton>
                  )}
                </TableCell>
              </TableRow>

              <TableRow>
                <TableCell component="th" scope="row">
                  <strong>User ID:</strong>
                </TableCell>
                <TableCell
                  align="left"
                  id={"communication-user-id"}>{rootStore.historyTableStore.selectedHistoryItem.userId}</TableCell>
                <TableCell></TableCell>

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
                <TableCell>
                  {
                    rootStore.historyTableStore.selectedHistoryItem.gatewayId !== null && (
                      <Button
                        id={"view-gateway-button"}
                        sx={{width: "220px"}}
                        variant={"contained"}
                        endIcon={<StorageRoundedIcon/>}
                        onClick={() => window.open(`/gateways?gatewayId=${rootStore.historyTableStore.selectedHistoryItem!.gatewayId}`, "_blank")}
                      >
                        Manage Gateway
                      </Button>
                    )
                  }
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
                <TableCell>
                  {
                    rootStore.historyTableStore.selectedHistoryItem.scheduleId !== null && (
                      <Button
                        sx={{width: "220px"}}
                        id={"view-schedule-button"}
                        variant={"contained"}
                        endIcon={<ScheduleSendRoundIcon/>}
                        onClick={() => window.open(`/schedules?scheduleId=${rootStore.historyTableStore.selectedHistoryItem!.scheduleId}`, "_blank")}
                      >
                        Manage Schedule
                      </Button>
                    )

                  }
                </TableCell>
              </TableRow>


            </TableBody>
          </Table>
        </TableContainer>
      )}</>
  )
})

export default InspectHistoryModalDescription
