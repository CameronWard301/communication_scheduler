import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, TextField, Typography, useTheme} from "@mui/material";
import {useNavigate} from "react-router-dom";
import ArrowBackRoundedIcon from '@mui/icons-material/ArrowBackRounded';
import {useStore} from "../../context/StoreContext.tsx";
import {useContext, useEffect} from "react";
import {useGatewayService} from "../../service/GatewayService.ts";
import {observer} from "mobx-react-lite";
import Box from "@mui/material/Box";
import CustomTooltip from "../../components/tooltip";
import CloseIcon from "@mui/icons-material/Close";
import SaveRoundedIcon from '@mui/icons-material/SaveRounded';
import LoadingButton from "@mui/lab/LoadingButton";
import {SnackbarContext} from "../../context/SnackbarContext.tsx";
import {ConfirmModal} from "../../components/modal";
import {ReviewGatewayTable} from "../../components/modal/gateway";


const EditGateway = observer(() => {
  const navigate = useNavigate();
  const rootStore = useStore();
  const {createGateway} = useGatewayService();
  const snackbar = useContext(SnackbarContext);
  const theme = useTheme();

  useEffect(() => {
    rootStore.gatewayAddStore.resetGateway();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleGatewaySave = () => {
    if (!rootStore.gatewayAddStore.fieldsAreValid()) {
      snackbar.addSnackbar("Please fix the field errors and try again", "error");
      return;
    }
    rootStore.gatewayAddStore.setConfirmModalOpen(true);
  }


  return (
    <>

      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12} mb={2}>
          <Button sx={{mb: 1}} variant="contained" color="primary" onClick={() => navigate(-1)}
                  startIcon={<ArrowBackRoundedIcon/>}>Go back</Button>
          <Typography variant="h1" fontSize={"4rem"} id={"preferences-page-heading"}>Add Communication
            Gateway</Typography>
        </Grid>

        <>
          <ConfirmModal confirmIcon={<SaveRoundedIcon/>}
                        open={rootStore.gatewayAddStore.confirmModalOpen}
                        setOpen={rootStore.gatewayAddStore.setConfirmModalOpen}
                        loading={rootStore.gatewayAddStore.isLoading}
                        heading={"Add Gateway"}
                        confirmText={"Add Gateway"}
                        onConfirm={() => {
                          createGateway(rootStore.gatewayAddStore.gateway);
                        }}
                        description={<ReviewGatewayTable gateway={rootStore.gatewayAddStore.gateway}/>}
          />

          <Grid xs={12} container spacing={2}>
            <Grid xs={12} lg={6} m={1} ml={2}>
              <Box>
                <Typography variant={"h4"} display={"inline-block"} id={"add-gateway-name-title"}>Gateway Name<span
                  style={{color: theme.palette.error.main}}>*</span></Typography>
                <CustomTooltip message="A friendly name to identify the gateway" ariaLabelTopic={"Gateway name"}/>
              </Box>
              <Box>
                <TextField label="Gateway Name" type="text" variant="outlined" margin={"normal"}
                           fullWidth
                           required
                           value={rootStore.gatewayAddStore.gateway.friendlyName} id={"gateway-name-input"}
                           error={!rootStore.gatewayAddStore.isFriendlyNameValid()}
                           helperText={!rootStore.gatewayAddStore.isFriendlyNameValid() ? "Please enter a name" : ""}
                           onChange={(event) => rootStore.gatewayAddStore.setGatewayName(event.target.value)}/>
              </Box>
            </Grid>
            <Grid xs={12} lg={6} m={1} ml={2}>
              <Box>
                <Typography variant={"h4"} display={"inline-block"} id={"add-endpoint-url-title"}>Endpoint URL<span
                  style={{color: theme.palette.error.main}}>*</span></Typography>
                <CustomTooltip message="The URL to connect to the gateway" ariaLabelTopic={"Endpoint URL"}/>
              </Box>
              <Box>
                <TextField label="Gateway URL" type="text" variant="outlined" margin={"normal"}
                           fullWidth
                           required
                           error={!rootStore.gatewayAddStore.isEndpointUrlValid()}
                           helperText={!rootStore.gatewayAddStore.isEndpointUrlValid() ? "Please enter a URL" : ""}
                           value={rootStore.gatewayAddStore.gateway.endpointUrl} id={"gateway-url-input"}
                           onChange={(event) => rootStore.gatewayAddStore.setGatewayEndpointUrl(event.target.value)}/>
              </Box>
            </Grid>
            <Grid xs={12} lg={6} m={1} ml={2}>
              <Box>
                <Typography variant={"h4"} display={"inline-block"}
                            id={"add-gateway-description-title"}>Description</Typography>
                <CustomTooltip message="An optional description of this gateway" ariaLabelTopic={"Gateway description"}/>
              </Box>
              <Box>
                <TextField label="Gateway Name" type="text" variant={"outlined"} margin={"normal"}
                           multiline
                           fullWidth
                           rows={4}
                           value={rootStore.gatewayAddStore.gateway.description} id={"gateway-description-input"}
                           onChange={(event) => rootStore.gatewayAddStore.setGatewayDescription(event.target.value)}/>

              </Box>
            </Grid>
          </Grid>
          <Grid xs={12} container spacing={2} mt={2}>
            <Grid xs={12} lg={3} ml={2}>
              <Button
                variant="contained"
                endIcon={<CloseIcon/>}
                id={"cancel-modal-button"}
                color={"info"}
                fullWidth
                sx={{height: 56}}
                onClick={() => {
                  navigate(-1)
                }}
              >
                Cancel
              </Button>
            </Grid>

            <Grid xs={12} lg={3} marginTop={"auto"}>
              <LoadingButton loading={rootStore.gatewayAddStore.isLoading} variant="contained" fullWidth
                             sx={{height: 56}}
                             disabled={!rootStore.gatewayAddStore.fieldsAreValid()}
                             color={"primary"} id={"confirm-add-button"} endIcon={<SaveRoundedIcon/>}
                             onClick={() => handleGatewaySave()}>
                Add Gateway
              </LoadingButton>
            </Grid>
          </Grid>

        </>
      </Grid>
    </>
  )
})

export default EditGateway
