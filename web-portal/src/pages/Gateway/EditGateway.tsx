import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, TextField, Typography, useTheme} from "@mui/material";
import {useNavigate, useParams} from "react-router-dom";
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
import DeleteRounded from "@mui/icons-material/DeleteRounded";
import {GatewayChangesTable, GatewayModal} from "../../components/modal";
import {ReviewGatewayTable} from "../../components/modal/gateway";
import {SnackbarContext} from "../../context/SnackbarContext.tsx";


const EditGateway = observer(() => {
  const navigate = useNavigate();
  const rootStore = useStore();
  const theme = useTheme();
  const params = useParams();
  const {getGatewayById, deleteGatewayById, updateGateway} = useGatewayService();
  const snackbar = useContext(SnackbarContext);

  useEffect(() => {
    if (params.id) {
      getGatewayById(params.id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [params.id]);

  const handleGatewayUpdate = () => {
    if (!rootStore.gatewayEditStore.hasGatewayChanged()) {
      snackbar.addSnackbar("No changes to save, please edit a field and try again", "info");
      return;
    }
    rootStore.gatewayEditStore.setConfirmModalOpen(true);
  }


  return (
    <>

      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12} mb={2}>
          <Button sx={{mb: 1}} variant="contained" color="primary" onClick={() => navigate(-1)}
                  startIcon={<ArrowBackRoundedIcon/>}>Go back</Button>
          <Typography variant="h1" fontSize={"4rem"} id={"preferences-page-heading"}>Edit Communication
            Gateway</Typography>
        </Grid>
        {rootStore.gatewayEditStore.storedGateway === null && (
          <Grid xs={12}>
            <Typography variant={"h6"}>Could not find gateway with id: {params.id}</Typography>
          </Grid>
        )}
        {rootStore.gatewayEditStore.storedGateway !== null && rootStore.gatewayEditStore.updatedGateway !== null && (
          <>
            <GatewayModal confirmIcon={<DeleteRounded/>}
                          gateway={rootStore.gatewayEditStore.storedGateway}
                          open={rootStore.gatewayEditStore.deleteModalOpen}
                          setOpen={rootStore.gatewayEditStore.setDeleteModalOpen}
                          affectedSchedules={rootStore.gatewayEditStore.affectedSchedules}
                          isLoading={rootStore.gatewayEditStore.isLoading}
                          heading={"Delete Gateway"}
                          confirmText={"Delete"}
                          onConfirm={() => {
                            deleteGatewayById(rootStore.gatewayEditStore.storedGateway!);
                          }}
                          description={<ReviewGatewayTable gateway={rootStore.gatewayEditStore.storedGateway}/>}
                          affectedScheduleTooltip={"Deleting this gateway will cause the number of schedules shown to stop working, its best to update the schedules to use a new gateway first by viewing the affected schedules and performing a bulk action."}/>

            <GatewayModal confirmIcon={<SaveRoundedIcon/>}
                          gateway={rootStore.gatewayEditStore.storedGateway}
                          open={rootStore.gatewayEditStore.confirmModalOpen}
                          setOpen={rootStore.gatewayEditStore.setConfirmModalOpen}
                          affectedSchedules={rootStore.gatewayEditStore.affectedSchedules}
                          isLoading={rootStore.gatewayEditStore.isLoading}
                          heading={"Modify Gateway"}
                          confirmText={"Modify"}
                          onConfirm={() => {
                            updateGateway(rootStore.gatewayEditStore.updatedGateway!);
                          }}
                          description={<GatewayChangesTable/>}
                          affectedScheduleTooltip={"Updating this gateway will affect the number of schedules shown here, make sure that your new gateway details are correct."}
            />

            <Grid xs={12}>
              <Typography variant={"body1"}><strong>ID: </strong>{rootStore.gatewayEditStore.updatedGateway.id}
              </Typography>
              <Typography variant={"body1"}><strong>Date
                Created: </strong>{rootStore.gatewayEditStore.updatedGateway.dateCreated}</Typography>

            </Grid>
            <Grid xs={12} container spacing={2}>
              <Grid xs={12} lg={6} m={1} ml={2}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"} id={"edit-gateway-name-title"}>Gateway Name<span
                    style={{color: theme.palette.error.main}}>*</span></Typography>
                  <CustomTooltip message="A friendly name to identify the gateway" ariaLabelTopic={"Gateway name"}/>
                </Box>
                <Box>
                  <TextField label="Gateway Name" type="text" variant="outlined" margin={"normal"}
                             fullWidth
                             required
                             error={!rootStore.gatewayEditStore.isFriendlyNameValid()}
                             helperText={!rootStore.gatewayEditStore.isFriendlyNameValid() ? "Please enter a name" : ""}
                             value={rootStore.gatewayEditStore.updatedGateway.friendlyName} id={"gateway-name-input"}
                             onChange={(event) => rootStore.gatewayEditStore.setGatewayName(event.target.value)}/>
                </Box>
              </Grid>
              <Grid xs={12} lg={6} m={1} ml={2}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"} id={"edit-gateway-url-title"}>Endpoint URL<span
                    style={{color: theme.palette.error.main}}>*</span></Typography>
                  <CustomTooltip message="The URL to connect to the gateway" ariaLabelTopic={"Endpoint URL"}/>
                </Box>
                <Box>
                  <TextField label="Gateway Name" type="text" variant="outlined" margin={"normal"}
                             fullWidth
                             required
                             helperText={!rootStore.gatewayEditStore.isEndpointUrlValid() ? "Please enter a URL" : ""}
                             error={!rootStore.gatewayEditStore.isEndpointUrlValid()}
                             value={rootStore.gatewayEditStore.updatedGateway.endpointUrl} id={"gateway-url-input"}
                             onChange={(event) => rootStore.gatewayEditStore.setGatewayEndpointUrl(event.target.value)}/>
                </Box>
              </Grid>
              <Grid xs={12} lg={6} m={1} ml={2}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"}
                              id={"edit-gateway-description-title"}>Description</Typography>
                  <CustomTooltip message="An optional description of this gateway" ariaLabelTopic={"Gateway Description"}/>
                </Box>
                <Box>
                  <TextField label="Gateway Name" type="text" variant={"outlined"} margin={"normal"}
                             multiline
                             fullWidth
                             rows={4}
                             value={rootStore.gatewayEditStore.updatedGateway.description}
                             id={"gateway-description-input"}
                             onChange={(event) => rootStore.gatewayEditStore.setGatewayDescription(event.target.value)}/>

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

              <Grid xs={12} lg={3}>
                <Button
                  variant="contained"
                  endIcon={<DeleteRounded/>}
                  id={"delete-gateway-button"}
                  color={"secondary"}
                  fullWidth
                  sx={{height: 56}}
                  onClick={() => {
                    rootStore.gatewayEditStore.setDeleteModalOpen(true)
                  }}
                >
                  Delete Gateway
                </Button>
              </Grid>

              <Grid xs={12} lg={3} marginTop={"auto"}>
                <LoadingButton loading={rootStore.gatewayTableStore.isLoading} variant="contained" fullWidth
                               sx={{height: 56}} disabled={!rootStore.gatewayEditStore.fieldsAreValid()}
                               color={"primary"} id={"confirm-edit-button"} endIcon={<SaveRoundedIcon/>}
                               onClick={() => handleGatewayUpdate()}>
                  Modify
                </LoadingButton>
              </Grid>
            </Grid>

          </>


        )}
      </Grid>
    </>
  )
})

export default EditGateway
