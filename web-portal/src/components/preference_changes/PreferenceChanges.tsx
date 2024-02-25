import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from "@mui/material";
import {observer} from "mobx-react-lite";
import {useStore} from "../../context/StoreContext.tsx";

export const PreferenceChanges = observer(() => {

  const getTimeUnitString = (unit: string) => {
    unit = unit.toLowerCase();
    if (unit === "s") {
      return "Seconds";
    } else if (unit === "m") {
      return "Minutes";
    } else if (unit === "h") {
      return "Hours";
    } else if (unit === "d") {
      return "Days";
    } else {
      return "Unknown";
    }
  }

  const rootStore = useStore();
  return (
    <>
      <Typography variant={"body1"} pb={2}>Are you sure you want to make the following changes?</Typography>
      <TableContainer component={Paper}>
        <Table sx={{minWidth: 150}} aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell>Option</TableCell>
              <TableCell align="right">Old Value</TableCell>
              <TableCell align="right">New Value</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rootStore.preferencesStore.hasMaximumAttemptsChanged() && (
              <TableRow>
                <TableCell component="th" scope="row">
                  Maximum Attempts
                </TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.currentMaximumAttempts == 0 ? "Unlimited" : rootStore.preferencesStore.currentMaximumAttempts}</TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.newMaximumAttempts == 0 ? "Unlimited" : rootStore.preferencesStore.newMaximumAttempts}</TableCell>
              </TableRow>
            )}
            {rootStore.preferencesStore.hasGatewayTimeoutChanged() && (
              <TableRow>
                <TableCell component="th" scope="row">
                  Gateway Timeout
                </TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.currentGatewayTimeout} {getTimeUnitString(rootStore.preferencesStore.currentGatewayTimeoutTime)}</TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.newGatewayTimeout} {getTimeUnitString(rootStore.preferencesStore.newGatewayTimeoutTime)}</TableCell>
              </TableRow>
            )}
            {rootStore.preferencesStore.hasBackoffCoefficientChanged() && (
              <TableRow>
                <TableCell component="th" scope="row">
                  Backoff Coefficient
                </TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.currentBackoffCoefficient == 1 ? "Disabled" : rootStore.preferencesStore.currentBackoffCoefficient}</TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.newBackoffCoefficient == 1 ? "Disabled" : rootStore.preferencesStore.newBackoffCoefficient}</TableCell>
              </TableRow>
            )}
            {rootStore.preferencesStore.hasInitialIntervalChanged() && (
              <TableRow>
                <TableCell component="th" scope="row">
                  Initial Interval
                </TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.currentInitialInterval} {getTimeUnitString(rootStore.preferencesStore.currentInitialIntervalTime)}</TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.newInitialInterval} {getTimeUnitString(rootStore.preferencesStore.newInitialIntervalTime)}</TableCell>
              </TableRow>
            )}

            {rootStore.preferencesStore.hasMaximumIntervalChanged() && (
              <TableRow>
                <TableCell component="th" scope="row">
                  Maximum Interval
                </TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.currentMaximumInterval == 0 ? "No Limit" : rootStore.preferencesStore.currentMaximumInterval as unknown as string + " " + getTimeUnitString(rootStore.preferencesStore.currentMaximumIntervalTime)}</TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.newMaximumInterval == 0 ? "No Limit" : rootStore.preferencesStore.newMaximumInterval as unknown as string + " " + getTimeUnitString(rootStore.preferencesStore.newMaximumIntervalTime)}</TableCell>
              </TableRow>
            )}

            {rootStore.preferencesStore.hasStartToCloseTimeoutChanged() && (
              <TableRow>
                <TableCell component="th" scope="row">
                  Start To Close Timeout
                </TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.currentStartToCloseTimeout == 0 ? "No Limit" : rootStore.preferencesStore.currentStartToCloseTimeout as unknown as string + " " + getTimeUnitString(rootStore.preferencesStore.currentStartToCloseTimeoutTime)}</TableCell>
                <TableCell
                  align="right">{rootStore.preferencesStore.newStartToCloseTimeout == 0 ? "No Limit" : rootStore.preferencesStore.newStartToCloseTimeout as unknown as string + " " + getTimeUnitString(rootStore.preferencesStore.newStartToCloseTimeoutTime)}</TableCell>
              </TableRow>
            )}

          </TableBody>
        </Table>
      </TableContainer>


    </>


  )
})
