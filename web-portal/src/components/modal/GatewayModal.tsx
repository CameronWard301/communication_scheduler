import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import CloseIcon from "@mui/icons-material/Close";
import {
  Button,
  Fade,
  Modal,
  Paper,
  Table,
  TableBody,
  TableCell, TableContainer,
  TableHead,
  TableRow,
  Typography,
  useTheme
} from "@mui/material";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Unstable_Grid2";
import ScheduleSendRoundIcon from '@mui/icons-material/ScheduleSend';
import {Gateway} from "../../models/Gateways.ts";
import {useStore} from "../../context/StoreContext.tsx";
import CustomTooltip from "../tooltip";
import {useGatewayService} from "../../service/GatewayService.ts";
import LoadingButton from "@mui/lab/LoadingButton";


type Props = {
  open: boolean;
  setOpen: (show: boolean) => void;
  heading: string
  confirmIcon: React.ReactNode;
  confirmText: string;
  onConfirm: () => void;
  gateway: Gateway;
  affectedScheduleTooltip: string;
};

const GatewayModal = observer(
  ({
     open,
     setOpen,
     gateway,
     affectedScheduleTooltip,
     heading,
     confirmIcon,
     confirmText,
     onConfirm
   }: Props) => {
    const theme = useTheme();
    const rootStore = useStore();
    const {getAffectedSchedules} = useGatewayService();
    const style = {
      maxWidth: "60%",
      maxHeight: "93vh",
      backgroundColor: "background.paper",
      border: `2px solid ${theme.palette.primary.main}`,
      borderRadius: 2,
      boxShadow: 24,
      p: 4,
      overflowY: "auto",
    };

    useEffect(() => {
      getAffectedSchedules(gateway.id);
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [rootStore.gatewayTableStore.selectedGateway]);

    return (
      <Modal
        aria-labelledby="transition-modal-title"
        aria-describedby="transition-modal-description"
        open={open}
        onClose={() => {
          setOpen(false);
        }}
        closeAfterTransition
      >
        <Fade in={open}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              minHeight: "100vh",
            }}
          >
            <Box sx={style}>
              <Grid container spacing={2}>
                <Grid xs={12}>
                  <Typography id="transition-modal-title" variant="h6" component="h2">
                    {heading}
                  </Typography>
                </Grid>
                <Grid xs={12}>
                  <TableContainer component={Paper}>
                    <Table sx={{minWidth: 60}} aria-label="gateway review table">
                      <TableHead>
                        <TableRow>
                          <TableCell>Field</TableCell>
                          <TableCell align="left">Value</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>

                        <TableRow>
                          <TableCell component="th" scope="row">
                            <strong>ID:</strong>
                          </TableCell>
                          <TableCell
                            align="left" id={"gateway-id"}>{gateway.id}</TableCell>
                        </TableRow>

                        <TableRow>
                          <TableCell component="th" scope="row">
                            <strong>Friendly Name:</strong>
                          </TableCell>
                          <TableCell
                            align="left" id={"gateway-friendly-name"}>{gateway.friendlyName}</TableCell>
                        </TableRow>

                        <TableRow>
                          <TableCell component="th" scope="row">
                            <strong>Endpoint URL:</strong>
                          </TableCell>
                          <TableCell
                            align="left" id={"gateway-endpoint-url"}>{gateway.endpointUrl}</TableCell>
                        </TableRow>

                        <TableRow>
                          <TableCell component="th" scope="row">
                            <strong>Date created:</strong>
                          </TableCell>
                          <TableCell
                            align="left" id={"gateway-date-created"}>{gateway.dateCreated}</TableCell>
                        </TableRow>

                        <TableRow>
                          <TableCell component="th" scope="row" sx={{color: rootStore.gatewayTableStore.affectedSchedules > 0 ? theme.palette.warning.main : theme.palette.success.main}}>
                            <strong>Schedules Affected:</strong>
                            {
                              rootStore.gatewayTableStore.affectedSchedules > 0 &&
                              <CustomTooltip
                                message={affectedScheduleTooltip}/>
                            }
                            {
                              rootStore.gatewayTableStore.affectedSchedules == 0 &&
                                <CustomTooltip
                                    message={"Modifying or deleting this gateway will not have any effect on existing schedules"}/>
                            }

                          </TableCell>
                          <TableCell
                            align="left" id={"gateway-date-created"}
                            sx={{color: rootStore.gatewayTableStore.affectedSchedules > 0 ? theme.palette.warning.main : theme.palette.success.main}}>
                            {rootStore.gatewayTableStore.affectedSchedules}
                          </TableCell>

                        </TableRow>


                      </TableBody>
                    </Table>
                  </TableContainer>
                  <Box id="transition-modal-description">

                  </Box>
                </Grid>


                <Grid xs={12} lg={4}>
                  <Button
                    variant="contained"
                    endIcon={<CloseIcon/>}
                    id={"cancel-modal-button"}
                    color={"info"}
                    fullWidth
                    sx={{height: 56}}
                    onClick={() => {
                      setOpen(false);
                    }}
                  >
                    Cancel
                  </Button>
                </Grid>

                <Grid xs={12} lg={4}>
                  <Button
                    variant="contained"
                    endIcon={<ScheduleSendRoundIcon/>}
                    id={"view-affected-schedules-modal-button"}
                    color={"secondary"}
                    fullWidth
                    sx={{height: 56}}
                    onClick={() => {
                      setOpen(false);
                    }}
                  >
                    View Affected Schedules
                  </Button>
                </Grid>

                <Grid xs={12} lg={4} marginTop={"auto"}>
                  <LoadingButton loading={rootStore.gatewayTableStore.isLoading} onClick={onConfirm} endIcon={confirmIcon} variant="contained" fullWidth sx={{height: 56}}
                          color={"primary"} id={"confirm-modal-button"}>
                    <span>{confirmText}</span>
                  </LoadingButton>
                </Grid>
              </Grid>
            </Box>
          </Box>
        </Fade>
      </Modal>
    );
  }
);

export default GatewayModal;
