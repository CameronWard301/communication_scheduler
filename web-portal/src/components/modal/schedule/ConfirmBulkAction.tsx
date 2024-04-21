import {observer} from "mobx-react-lite";
import {useStore} from "../../../context/StoreContext.tsx";
import {Paper, Table, TableBody, TableCell, TableContainer, TableRow} from "@mui/material";

const ConfirmBulkAction = observer(() => {
  const rootStore = useStore();
  return (
    <TableContainer component={Paper}>
      <Table aria-label="schedule bulk action review table">

        <TableBody>


          <TableRow>
            <TableCell component="th" scope="row" width={"50%"}>
              <strong>Action:</strong>
            </TableCell>
            <TableCell
              align="left"
              id={"bulk-action-type"}>{rootStore.bulkActionStore.bulkActionType}</TableCell>
          </TableRow>

          <TableRow>
            <TableCell component="th" scope="row">
              <strong>Schedules Selected:</strong>
            </TableCell>
            <TableCell
              align="left" id={"total-schedules-affected"}>{rootStore.scheduleTableStore.totalCount}</TableCell>
          </TableRow>


        </TableBody>
      </Table>
    </TableContainer>


  )
})

export default ConfirmBulkAction;
