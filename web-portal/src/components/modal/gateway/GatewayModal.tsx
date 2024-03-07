import React, {useEffect} from "react";
import {observer} from "mobx-react-lite";
import CloseIcon from "@mui/icons-material/Close";
import {Button, Fade, Modal, Typography, useTheme} from "@mui/material";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Unstable_Grid2";
import ScheduleSendRoundIcon from '@mui/icons-material/ScheduleSend';
import {Gateway} from "../../../models/Gateways.ts";
import {useGatewayService} from "../../../service/GatewayService.ts";
import LoadingButton from "@mui/lab/LoadingButton";
import {useNavigate} from "react-router-dom";
import CustomTooltip from "../../tooltip";


type Props = {
  open: boolean;
  setOpen: (show: boolean) => void;
  heading: string
  confirmIcon: React.ReactNode;
  description: React.ReactNode;
  affectedScheduleTooltip: string;
  confirmText: string;
  onConfirm: () => void;
  gateway: Gateway;
  affectedSchedules: number;
  isLoading: boolean;
};

const GatewayModal = observer(
  ({
     open,
     setOpen,
     gateway,
     description,
     heading,
     confirmIcon,
     confirmText,
     onConfirm,
     affectedScheduleTooltip,
     affectedSchedules,
     isLoading
   }: Props) => {
    const theme = useTheme();
    const {getAffectedSchedules} = useGatewayService();
    const navigate = useNavigate();
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
    }, [gateway]);

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
                  {description}
                </Grid>

                <Grid xs={12}>

                  <Grid
                    sx={{color: affectedSchedules > 0 ? theme.palette.warning.main : theme.palette.success.main}}
                    xs={4}>
                    <Typography display={"inline-block"} fontWeight={"bold"}>Schedules Affected: </Typography>
                    <span>
                      {
                        affectedSchedules > 0 &&
                          <CustomTooltip message={affectedScheduleTooltip}/>
                      }
                      {
                        affectedSchedules == 0 &&
                          <CustomTooltip
                              message={"Modifying or deleting this gateway will not have any effect on existing schedules"}/>
                      }
                    </span>
                    <Typography
                      border={"2px solid " + (affectedSchedules > 0 ? theme.palette.warning.main : theme.palette.success.main)}
                      borderRadius={2}
                      px={2}
                      py={1}
                      ml={4}
                      fontWeight={"bold"}
                      display={"inline-block"}
                      align="left" id={"affected-schedules-count"}
                      sx={{color: affectedSchedules > 0 ? theme.palette.warning.main : theme.palette.success.main}}>
                      {affectedSchedules}
                    </Typography>


                  </Grid>


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
                      navigate("/schedules?gatewayId=" + gateway.id);
                      setOpen(false);
                    }}
                  >
                    View Affected Schedules
                  </Button>
                </Grid>

                <Grid xs={12} lg={4} marginTop={"auto"}>
                  <LoadingButton loading={isLoading} onClick={onConfirm} endIcon={confirmIcon} variant="contained"
                                 fullWidth sx={{height: 56}}
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
