import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {Gateway} from "../../../models/Gateways.ts";


export interface ConfirmGatewayTableProps {
  gateway: Gateway;
  idPrefix?: string;
}

const ReviewGatewayTable = ({gateway, idPrefix = ""}: ConfirmGatewayTableProps) => {

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
            gateway.id !== "0" && (
              <TableRow>
                <TableCell component="th" scope="row">
                  <strong>ID:</strong>
                </TableCell>
                <TableCell
                  align="left" id={idPrefix + "gateway-id"}>{gateway.id}</TableCell>
              </TableRow>
            )
          }

          <TableRow>
            <TableCell component="th" scope="row">
              <strong>Friendly Name:</strong>
            </TableCell>
            <TableCell
              align="left" id={idPrefix + "gateway-friendly-name"}>{gateway.friendlyName}</TableCell>
          </TableRow>

          <TableRow>
            <TableCell component="th" scope="row">
              <strong>Endpoint URL:</strong>
            </TableCell>
            <TableCell
              align="left" id={idPrefix + "gateway-endpoint-url"}>{gateway.endpointUrl}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell component="th" scope="row">
              <strong>Description:</strong>
            </TableCell>
            <TableCell
              align="left" id={idPrefix + "gateway-description"}>{gateway.description}</TableCell>
          </TableRow>

          {gateway.dateCreated != "" && (
            <TableRow>
              <TableCell component="th" scope="row">
                <strong>Date created:</strong>
              </TableCell>
              <TableCell
                align="left" id={idPrefix + "gateway-date-created"}>{gateway.dateCreated}</TableCell>
            </TableRow>
          )}


        </TableBody>
      </Table>
    </TableContainer>
  )

}

export default ReviewGatewayTable;
