import {useStore} from "../../context/StoreContext.tsx";
import {Button, Chip, useTheme} from "@mui/material";
import {GridColDef, GridRenderCellParams} from "@mui/x-data-grid";
import Stack from "@mui/material/Stack";
import StopCircleRoundedIcon from '@mui/icons-material/StopCircleRounded';
import IconButton from "@mui/material/IconButton";
import VisibilityRoundedIcon from '@mui/icons-material/VisibilityRounded';
import {getStatusColour, HistoryItem} from "../../models/History.ts";
import Box from "@mui/material/Box";

const useHistoryGridDef = () => {
  const rootStore = useStore();
  const theme = useTheme();

  const historyTablePageColumns: GridColDef[] = [
    {
      field: 'status', headerName: 'Status', flex: 0.2, minWidth: 120, filterable: false,
      renderCell: (params: GridRenderCellParams<HistoryItem>) => {
        return (
          <Box display="flex" alignContent={"center"} alignItems={"center"} height={"100%"} justifyContent={"center"}
               width={"100%"}>
            <Chip sx={{backgroundColor: getStatusColour(params.row.status, theme), width: "100%"}}
                  label={params.row.status}/>
          </Box>
        )
      }
    },
    {field: 'id', headerName: 'Run ID', flex: 0.2, minWidth: 300, filterable: false},
    {field: 'userId', headerName: 'User ID', flex: 0.5, minWidth: 130, filterable: false},
    {field: 'gatewayId', headerName: 'Gateway ID', flex: 0.7, minWidth: 130, filterable: false},
    {field: 'gatewayName', headerName: 'Gateway Name', flex: 0.6, minWidth: 130, filterable: false},
    {field: 'scheduleId', headerName: 'Schedule ID', flex: 0.7, minWidth: 130, filterable: false},
    {field: 'startTime', headerName: 'Start Time', flex: 0.2, minWidth: 200, filterable: false},
    {field: 'endTime', headerName: 'End Time', flex: 0.2, minWidth: 200, filterable: false},
    {
      field: 'actions', headerName: 'Actions', flex: 0.3, minWidth: 180, filterable: false,
      renderCell: (params: GridRenderCellParams<HistoryItem>) => {
        return (
          <Stack direction={"row"} spacing={3} sx={{display: 'flex', height: "100%", py: 1}}>
            <Button
              aria-label={"View Communication " + params.row.id}
              variant="contained"
              color="primary"
              size="small"
              id={`view-communication-${params.row.id}`}
              endIcon={<VisibilityRoundedIcon/>}
              onClick={() => {
                rootStore.historyTableStore.setSelectedHistoryItem(params.row);
                rootStore.historyTableStore.setOpenInspectModal(true);
              }}
            >
              View
            </Button>
            {
              params.row.status == "Running" && (
                <IconButton sx={{"&:hover": {color: theme.palette.error.main}}} onClick={() => {
                  rootStore.historyTableStore.setSelectedHistoryItem(params.row);
                  rootStore.historyTableStore.setOpenConfirmStopModal(true);
                }}
                            id={`stop-communication-${params.row.id}`}>
                  <StopCircleRoundedIcon/>
                </IconButton>
              )
            }

          </Stack>

        );
      }
    },
  ];


  return {historyTablePageColumns};
}

export default useHistoryGridDef;
