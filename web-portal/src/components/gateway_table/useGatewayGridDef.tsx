import {useNavigate} from "react-router-dom";
import {GridColDef, GridRenderCellParams} from "@mui/x-data-grid";
import {Gateway} from "../../models/Gateways.ts";
import {Button} from "@mui/material";
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import Stack from "@mui/material/Stack";
import IconButton from "@mui/material/IconButton";
import {DeleteRounded} from "@mui/icons-material";

const useGatewayGridDef = () => {
  const navigate = useNavigate();

  const columns: GridColDef[] = [
    {field: 'id', headerName: 'Gateway ID', flex: 0.5, minWidth: 130},
    {field: 'friendlyName', headerName: 'Gateway Name', flex: 0.5, minWidth: 130},
    {field: 'description', headerName: 'Description', flex: 0.7, minWidth: 130},
    {field: 'endpointUrl', headerName: 'Endpoint Url', flex: 0.7, minWidth: 130},
    {field: 'dateCreated', headerName: 'Date Created', flex: 0.2, minWidth: 180},
    {
      field: 'actions', headerName: 'Actions', flex: 0.3, minWidth: 180,
      renderCell: (params: GridRenderCellParams<Gateway>) => {
        return (
          <Stack direction={"row"} spacing={3}>
            <Button
              aria-label={"Modify Gateway " + params.row.friendlyName}
              variant="contained"
              color="primary"
              size="small"
              endIcon={<EditRoundedIcon/>}
              onClick={() => navigate(`/gateway/${params.row.id}`)}
            >
              Modify
            </Button>
            <IconButton>
              <DeleteRounded/>
            </IconButton>
          </Stack>

        );
      }
    },
  ];

  return {columns};

}

export default useGatewayGridDef;
