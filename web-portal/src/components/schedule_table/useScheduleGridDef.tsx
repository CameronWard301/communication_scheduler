import {GridColDef, GridRenderCellParams} from "@mui/x-data-grid";
import {ScheduleStatus, ScheduleTableItem} from "../../models/Schedules.ts";
import Stack from "@mui/material/Stack";
import {Button, Typography, useTheme} from "@mui/material";
import EditRoundedIcon from "@mui/icons-material/EditRounded";
import {useNavigate} from "react-router-dom";
import {DeleteRounded} from "@mui/icons-material";
import IconButton from "@mui/material/IconButton";
import PauseCircleFilledRoundedIcon from '@mui/icons-material/PauseCircleFilledRounded';
import PlayCircleFilledRoundedIcon from '@mui/icons-material/PlayCircleFilledRounded';

const useScheduleGridDef = () => {
  const navigate = useNavigate();
  const theme = useTheme();

  const columns: GridColDef[] = [
    {field: 'id', headerName: 'Schedule ID', flex: 0.5, minWidth: 300, filterable: false},
    {
      field: 'status', headerName: 'Status', flex: 0.2, minWidth: 80, filterable: false,
      renderCell: (params: GridRenderCellParams<ScheduleTableItem>) => {
        return (<Typography variant={"body2"} sx={{color: params.row.status==ScheduleStatus.Running? theme.palette.success.main : theme.palette.warning.main, display: 'flex', height: "100%", py: 1, alignItems: 'center'}} >{params.row.status}</Typography>)
      }
    },
    {field: 'gatewayName', headerName: 'Gateway Name', flex: 0.5, minWidth: 130, filterable: false},
    {field: 'gatewayId', headerName: 'Gateway ID', flex: 0.5, minWidth: 300, filterable: false},
    {field: 'userId', headerName: 'User ID', flex: 0.2, minWidth: 180, filterable: false},
    {field: 'createdAt', headerName: 'Created At', flex: 0.2, minWidth: 180, filterable: false},
    {field: 'updatedAt', headerName: 'Updated At', flex: 0.2, minWidth: 180, filterable: false},
    {field: 'nextRun', headerName: 'Next Run', flex: 0.2, minWidth: 180, filterable: false},
    {field: 'lastRun', headerName: 'Last Run', flex: 0.2, minWidth: 180, filterable: false},
    {
      field: 'actions', headerName: 'Actions', width: 250, filterable: false,
      renderCell: (params: GridRenderCellParams<ScheduleTableItem>) => {
        return (
          <Stack direction={"row"} spacing={3} sx={{display: 'flex', height: "100%", py: 1}}>
            <Button
              aria-label={"Modify Schedule " + params.row.id}
              variant="contained"
              color="primary"
              size="small"
              id={`modify-schedule-${params.row.id}`}
              endIcon={<EditRoundedIcon/>}
              onClick={() => navigate(`/schedule/${params.row.id}`)}
            >
              Modify
            </Button>

            {
              params.row.status === ScheduleStatus.Paused && (
                <IconButton sx={{"&:hover": {color: theme.palette.success.main}}} onClick={() => {
                  // rootStore.gatewayTableStore.setSelectedGateway(params.row);
                  // rootStore.gatewayTableStore.setDeleteModalOpen(true);
                }}
                            id={`resume-schedule-${params.row.id}`}>
                  <PlayCircleFilledRoundedIcon/>
                </IconButton>
              )
            }
            {
              params.row.status === ScheduleStatus.Running && (
                <IconButton sx={{"&:hover": {color: theme.palette.warning.main}}} onClick={() => {
                  // rootStore.gatewayTableStore.setSelectedGateway(params.row);
                  // rootStore.gatewayTableStore.setDeleteModalOpen(true);
                }}
                            id={`pause-schedule-${params.row.id}`}>
                  <PauseCircleFilledRoundedIcon/>
                </IconButton>
              )
            }
            <IconButton sx={{"&:hover": {color: theme.palette.error.main}}} onClick={() => {
              // rootStore.gatewayTableStore.setSelectedGateway(params.row);
              // rootStore.gatewayTableStore.setDeleteModalOpen(true);
            }}
                        id={`delete-schedule-${params.row.id}`}>
              <DeleteRounded/>
            </IconButton>

          </Stack>
        );
      }
    }


  ];

  return {columns};
}

export default useScheduleGridDef;
