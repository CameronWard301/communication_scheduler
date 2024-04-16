import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, TextField, Typography} from "@mui/material";
import SaveRoundedIcon from '@mui/icons-material/SaveRounded';
import {observer} from "mobx-react-lite"
import Box from "@mui/material/Box";
import AllInclusiveRoundedIcon from '@mui/icons-material/AllInclusiveRounded';
import {useStore} from "../context/StoreContext.tsx";
import ArrowDownwardRoundedIcon from '@mui/icons-material/ArrowDownwardRounded';
import ArrowUpwardRoundedIcon from '@mui/icons-material/ArrowUpwardRounded';
import NotInterestedRoundedIcon from '@mui/icons-material/NotInterestedRounded';
import TimeSelection from "../components/time_selection";
import CustomTooltip from "../components/tooltip";
import {usePreferencesService} from "../service/PreferencesService.ts";
import {useContext, useEffect} from "react";
import {ConfirmModal} from "../components/modal";
import {PreferenceChanges} from "../components/preference_changes";
import {SnackbarContext} from "../context/SnackbarContext.tsx";
import LoadingButton from '@mui/lab/LoadingButton';


const Preferences = observer(() => {
  const rootStore = useStore();
  const {addSnackbar} = useContext(SnackbarContext);

  const preferencesService = usePreferencesService();

  useEffect(() => {
    preferencesService.getPreferences();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handeModalOpen = () => {
    if (rootStore.preferencesStore.hasChanged()) {
      rootStore.preferencesStore.setModalOpen(true);
      return;
    }
    addSnackbar("You haven't made any changes to save", "info");
  }

  const handleSave = () => {
    preferencesService.setPreferences();
  }

  return (
    <>
      <ConfirmModal
        loading={rootStore.preferencesStore.isLoading}
        open={rootStore.preferencesStore.modalOpen}
        confirmText={"Save Changes"}
        description={<PreferenceChanges/>}
        onConfirm={() => handleSave()}
        setOpen={rootStore.preferencesStore.setModalOpen}
        heading={"Save Changes"}
        confirmIcon={<SaveRoundedIcon/>}/>


      <Grid container spacing={4} justifyContent={"center"} alignItems={"center"} alignContent={"flex-start"}>
        <Grid xs={12} mb={2}>
          <Typography variant="h1" fontSize={"4rem"} id={"preferences-page-heading"}>Platform Configuration</Typography>
          <Typography variant="body1">Here you can configure the retry policies for failed communications. You can
            specify how many retry attempts should occur before terminating, how quickly the system should try again
            and
            how long between each retry</Typography>
        </Grid>
        <Grid xs={12}>
          <Box>
            <Typography variant={"h4"} display={"inline-block"} id={"maximum-attempts-title"}>Maximum
              Attempts</Typography>
            <CustomTooltip
              message="The maximum number of attempts to send the communication to the customer if the system encounters an error, or select unlimited attempts." ariaLabelTopic={"Maximum attempts"}/>
          </Box>
          <Box>
            <TextField label="Maximum Attempts" type="number" variant="outlined" margin={"normal"}
                       value={rootStore.preferencesStore.newMaximumAttempts}
                       id={"max-attempts-input"}
                       onChange={(event) => rootStore.preferencesStore.setMaximumAttempts(event.target.value)}/>
            <Button variant={rootStore.preferencesStore.newMaximumAttempts == 0 ? "contained" : "outlined"}
                    color={rootStore.preferencesStore.newMaximumAttempts == 0 ? "secondary" : "info"}
                    onClick={() => rootStore.preferencesStore.setMaximumAttempts("0")}
                    endIcon={<AllInclusiveRoundedIcon/>} id={"unlimited-maximum-attempts-btn"}
                    sx={{height: "56px", mt: 2, ml: 3}}>Unlimited Attempts</Button>
          </Box>
        </Grid>

        <Grid xs={12}>
          <Box>
            <Typography variant={"h4"} display={"inline-block"} id={"gateway-timeout-title"}>Gateway
              Timeout</Typography>
            <CustomTooltip
              message="The time to wait for a gateway to respond back with a status before retrying the gateway" ariaLabelTopic={"Gateway timeout"}/>
          </Box>
          <Box>
            <TextField label="Gateway Timeout" type="number" variant="outlined" margin={"normal"}
                       value={rootStore.preferencesStore.newGatewayTimeout} id={"gateway-timeout-input"}
                       onChange={(event) => rootStore.preferencesStore.setGatewayTimeout(event.target.value)}/>
            <TimeSelection keyId={"gateway-timeout-time"}
                           value={rootStore.preferencesStore.newGatewayTimeoutTime} onChange={(event) => {
              rootStore.preferencesStore.setGatewayTimeoutTime(event.target.value)
            }}/>
          </Box>
        </Grid>
        <Grid xs={12}>
          <Button variant="outlined"
                  color="primary"
                  id={"advanced-options-button"}
                  onClick={() => rootStore.preferencesStore.setAdvancedMode(!rootStore.preferencesStore.isAdvancedMode)}
                  endIcon={rootStore.preferencesStore.isAdvancedMode ? <ArrowUpwardRoundedIcon/> :
                    <ArrowDownwardRoundedIcon/>}
          >Advanced Options</Button>
        </Grid>
        {
          rootStore.preferencesStore.isAdvancedMode && (
            <>
              <Grid>
                <Typography variant={"body1"} id={"advanced-description"}>Below you can configure the retry policy. The
                  backoff coefficient is a
                  multiplier for the time it takes to retry the next attempt.
                  For example if the backoff coefficient is 2 the time between each retry doubles.
                  If the initial interval is 2, the platform will wait 2 seconds before retrying, then 4 seconds then 8
                  seconds. This number will
                  keep increasing unless there is a maximum limit of 100 seconds, then the platform will retry every 100
                  seconds after reaching this limit .</Typography>
              </Grid>

              <Grid xs={12}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"} id={"backoff-coefficient-title"}>Backoff
                    Coefficient</Typography>
                  <CustomTooltip message="The backoff coefficient to use for exponential backoff. To disable backoff, enter 1.0,
                                     the system will then use the initial interval to determine when the next retry should be." ariaLabelTopic={"Backoff coefficient"}/>
                </Box>
                <Box>
                  <TextField label="Backoff Coeeficient" type="number" variant="outlined" margin={"normal"}
                             value={rootStore.preferencesStore.newBackoffCoefficient} id={"backoff-coefficient-input"}
                             onChange={(event) => rootStore.preferencesStore.setBackoffCoefficient(event.target.value)}/>
                  <Button variant={rootStore.preferencesStore.newBackoffCoefficient == 1 ? "contained" : "outlined"}
                          color={rootStore.preferencesStore.newBackoffCoefficient == 1 ? "secondary" : "info"}
                          onClick={() => rootStore.preferencesStore.setBackoffCoefficient("1")}
                          endIcon={<NotInterestedRoundedIcon/>}
                          id={"disable-backoff-coefficient-btn"}
                          sx={{height: "56px", mt: 2, ml: 3}}>Disable</Button>
                </Box>
              </Grid>

              <Grid xs={12}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"} id={"initial-interval-title"}>Initial
                    Interval</Typography>
                  <CustomTooltip message="How soon should the system retry the failed step after encountering a failure.
                                     Subsequent retries will also be at this value if the backoff coefficient is set to 1 or less." ariaLabelTopic={"Initial interval"}/>
                </Box>
                <Box>
                  <TextField label="Initial Interval" type="number" variant="outlined" margin={"normal"}
                             value={rootStore.preferencesStore.newInitialInterval} id={"initial-interval-input"}
                             onChange={(event) => rootStore.preferencesStore.setInitialInterval(event.target.value)}/>
                  <TimeSelection keyId={"initial-interval-time"}
                                 value={rootStore.preferencesStore.newInitialIntervalTime} onChange={(event) => {
                    rootStore.preferencesStore.setInitialIntervalTime(event.target.value)
                  }}/>
                </Box>
              </Grid>

              <Grid xs={12}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"} id={"maximum-interval-title"}>Maximum
                    Interval</Typography>
                  <CustomTooltip message="The maximum amount of time between a retried communication. Set this value to set
                                     a limit to the exponential backoff coefficient, setting this to 0 means no limit." ariaLabelTopic={"Maximum interval"}/>
                </Box>
                <Box>
                  <TextField label="Maximum Interval" type="number" variant="outlined" margin={"normal"}
                             value={rootStore.preferencesStore.newMaximumInterval}
                             id={"maximum-interval-input"}
                             onChange={(event) => rootStore.preferencesStore.setMaximumInterval(event.target.value)}/>
                  <TimeSelection keyId={"maximum-interval-time"}
                                 value={rootStore.preferencesStore.newMaximumIntervalTime} onChange={(event) => {
                    rootStore.preferencesStore.setMaximumIntervalTime(event.target.value)
                  }}/>
                  <Button variant={rootStore.preferencesStore.newMaximumInterval == 0 ? "contained" : "outlined"}
                          color={rootStore.preferencesStore.newMaximumInterval == 0 ? "secondary" : "info"}
                          onClick={() => rootStore.preferencesStore.setMaximumInterval("0")}
                          endIcon={<NotInterestedRoundedIcon/>} id={"no-limit-maximum-interval-btn"}
                          sx={{height: "56px", mt: 2, ml: 3}}>No Limit</Button>
                </Box>
              </Grid>

              <Grid xs={12}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"} id={"start-to-close-timout-title"}>Start To Close
                    Timeout</Typography>
                  <CustomTooltip
                    message="The maximum amount of time it should take to send a communication, if the communication execution exceeds this limit then there are no more retries. Set to 0 to disable" ariaLabelTopic={"Start to close timeout help"}/>
                </Box>
                <Box>
                  <TextField label="Start To Close Timeout" type="number" variant="outlined" margin={"normal"}
                             value={rootStore.preferencesStore.newStartToCloseTimeout} id={"start-to-close-timeout-input"}
                             onChange={(event) => rootStore.preferencesStore.setStartToCloseTimeout(event.target.value)}/>
                  <TimeSelection keyId={"start-to-close-timeout-time"}
                                 value={rootStore.preferencesStore.newStartToCloseTimeoutTime} onChange={(event) => {
                    rootStore.preferencesStore.setStartToCloseTimeoutTime(event.target.value)
                  }}/>
                  <Button variant={rootStore.preferencesStore.newStartToCloseTimeout == 0 ? "contained" : "outlined"}
                          color={rootStore.preferencesStore.newStartToCloseTimeout == 0 ? "secondary" : "info"}
                          onClick={() => rootStore.preferencesStore.setStartToCloseTimeout("0")}
                          endIcon={<NotInterestedRoundedIcon/>} id={"no-limit-start-to-close-timeout-btn"}
                          sx={{height: "56px", mt: 2, ml: 3}}>No Limit</Button>
                </Box>
              </Grid>
            </>
          )
        }
        <Grid xs={12}>
          <LoadingButton variant="contained" color={"primary"} endIcon={<SaveRoundedIcon/>} size={"large"}
                         loadingPosition={"end"} loading={rootStore.preferencesStore.isLoading}
                         onClick={() => handeModalOpen()} id={"save-preferences-btn"}>Save
            Changes</LoadingButton>
        </Grid>
      </Grid>
    </>
  )
})

export default Preferences;
