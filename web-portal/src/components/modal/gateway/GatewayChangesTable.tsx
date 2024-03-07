import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from "@mui/material";
import {observer} from "mobx-react-lite";
import {useStore} from "../../../context/StoreContext";


const GatewayChangesTable = observer(() => {


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
            {rootStore.gatewayEditStore.hasFriendlyNameChanged() && (
              <TableRow>
                <TableCell component="th" scope="row" id={"friendly-name-confirm-table-cell"}>
                  Gateway Name
                </TableCell>
                <TableCell
                  align="right">{rootStore.gatewayEditStore.storedGateway?.friendlyName}</TableCell>
                <TableCell id={"friendly-name-new-value"}
                           align="right">{rootStore.gatewayEditStore.updatedGateway?.friendlyName}</TableCell>
              </TableRow>
            )}
            {rootStore.gatewayEditStore.hasEndpointUrlChanged() && (
              <TableRow>
                <TableCell component="th" scope="row" id={"endpoint-url-confirm-table-cell"}>
                  Gateway URL
                </TableCell>
                <TableCell
                  align="right">{rootStore.gatewayEditStore.storedGateway?.endpointUrl}</TableCell>
                <TableCell id={"endpoint-url-new-value"}
                           align="right">{rootStore.gatewayEditStore.updatedGateway?.endpointUrl}</TableCell>
              </TableRow>
            )}
            {rootStore.gatewayEditStore.hasDescriptionChanged() && (
              <TableRow>
                <TableCell component="th" scope="row" id={"description-confirm-table-cell"}>
                  Description
                </TableCell>
                <TableCell
                  align="right">{rootStore.gatewayEditStore.storedGateway?.description}</TableCell>
                <TableCell id={"description-new-value"}
                           align="right">{rootStore.gatewayEditStore.updatedGateway?.description}</TableCell>
              </TableRow>
            )}


          </TableBody>
        </Table>
      </TableContainer>


    </>


  )
})

export default GatewayChangesTable
