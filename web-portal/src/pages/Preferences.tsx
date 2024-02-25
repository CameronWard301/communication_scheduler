import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {
  Button,
  TextField,
  Tooltip,
  tooltipClasses,
  TooltipProps,
  Typography
} from "@mui/material";
import SaveRoundedIcon from '@mui/icons-material/SaveRounded';
import {styled} from "@mui/material/styles";
import React from "react";
import {observer} from "mobx-react-lite"
import IconButton from "@mui/material/IconButton";
import HelpRoundedIcon from '@mui/icons-material/HelpRounded';
import Box from "@mui/material/Box";
import AllInclusiveRoundedIcon from '@mui/icons-material/AllInclusiveRounded';
import {useStore} from "../context/StoreContext.tsx";
import ArrowDownwardRoundedIcon from '@mui/icons-material/ArrowDownwardRounded';
import ArrowUpwardRoundedIcon from '@mui/icons-material/ArrowUpwardRounded';
import NotInterestedRoundedIcon from '@mui/icons-material/NotInterestedRounded';
import TimeSelection from "../components/time_selection";

// adapted from: https://mui.com/material-ui/react-tooltip/
const HtmlTooltip = styled(({className, ...props}: TooltipProps) => (
  <Tooltip {...props} classes={{popper: className}} children={props.children}/>
))(({theme}) => ({
  [`& .${tooltipClasses.tooltip}`]: {
    backgroundColor: '#f5f5f9',
    color: 'rgba(0, 0, 0, 0.87)',
    maxWidth: 420,
    fontSize: theme.typography.pxToRem(12),
    border: '1px solid #dadde9',
  },
}));

const Preferences = observer(() => {
  const rootStore = useStore();
  return (
    <>
      <Grid container spacing={4} justifyContent={"center"} alignItems={"center"} alignContent={"flex-start"}>
        <Grid xs={12} mb={2}>
          <Typography variant="h1" fontSize={"4rem"}>Platform Configuration</Typography>
          <Typography variant="body1">Here you can configure the retry policies for failed communications. You can
            specify how many retry attempts should occur before terminating, how quickly the system should try again and
            how long between each retry</Typography>
        </Grid>
        <Grid xs={12}>
          <Box>
            <Typography variant={"h4"} display={"inline-block"}>Maximum Attempts</Typography>
            <HtmlTooltip placement={"right-start"}

                         title={
                           <React.Fragment>
                             <Typography variant={"body1"}>The maximum number of attempts to send the communication to
                               the
                               customer if
                               the system encounters an error, or select unlimited attempts.</Typography>
                           </React.Fragment>
                         }>
              <IconButton>
                <HelpRoundedIcon/>
              </IconButton>
            </HtmlTooltip>
          </Box>
          <Box>
            <TextField label="Maximum Attempts" type="number" variant="outlined" margin={"normal"}
                       value={rootStore.preferencesStore.newMaximumAttempts}
                       onChange={(event) => rootStore.preferencesStore.setMaximumAttempts(event.target.value)}/>
            <Button variant={rootStore.preferencesStore.newMaximumAttempts == 0 ? "contained" : "outlined"}
                    color={rootStore.preferencesStore.newMaximumAttempts == 0 ? "secondary" : "info"}
                    onClick={() => rootStore.preferencesStore.setMaximumAttempts("0")}
                    endIcon={<AllInclusiveRoundedIcon/>}
                    sx={{height: "56px", mt: 2, ml: 3}}>Unlimited Attempts</Button>
          </Box>
        </Grid>
        <Grid xs={12}>
          <Button variant="outlined"
                  color="primary"
                  onClick={() => rootStore.preferencesStore.setAdvancedMode(!rootStore.preferencesStore.isAdvancedMode)}
                  endIcon={rootStore.preferencesStore.isAdvancedMode ? <ArrowUpwardRoundedIcon/>: <ArrowDownwardRoundedIcon/>}
                  >Advanced Options</Button>
        </Grid>
        {
          rootStore.preferencesStore.isAdvancedMode && (
            <>
              <Grid>
                <Typography variant={"body1"}>Below you can configure the retry policy. The backoff coefficient is a multiplier for the time it takes to retry the next attempt.
                 For example if the backoff coefficient is 2 the time between each retry doubles.
                  If the initial interval is 2, the platform will wait 2 seconds before retrying, then 4 seconds then 8 seconds. This number will
                  keep increasing unless there is a maximum limit of 100 seconds, then the platform will retry every 100 seconds after reaching this limit .</Typography>
              </Grid>

              <Grid xs={12}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"}>Backoff Coefficient</Typography>
                  <HtmlTooltip placement={"right-start"}

                               title={
                                 <React.Fragment>
                                   <Typography variant={"body1"}>The backoff coefficient to use for exponential backoff. To disable backoff, enter 1.0,
                                     the system will then use the initial interval to determine when the next retry should be.</Typography>
                                 </React.Fragment>
                               }>
                    <IconButton>
                      <HelpRoundedIcon/>
                    </IconButton>
                  </HtmlTooltip>
                </Box>
                <Box>
                  <TextField label="Backoff Coeeficient" type="number" variant="outlined" margin={"normal"}
                             value={rootStore.preferencesStore.newBackoffCoefficient}
                             onChange={(event) => rootStore.preferencesStore.setBackoffCoefficient(event.target.value)}/>
                  <Button variant={rootStore.preferencesStore.newBackoffCoefficient == 1 ? "contained" : "outlined"}
                          color={rootStore.preferencesStore.newBackoffCoefficient == 1 ? "secondary" : "info"}
                          onClick={() => rootStore.preferencesStore.setBackoffCoefficient("1")}
                          endIcon={<NotInterestedRoundedIcon/>}
                          sx={{height: "56px", mt: 2, ml: 3}}>Disable</Button>
                </Box>
              </Grid>

              <Grid xs={12}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"}>Initial Interval</Typography>
                  <HtmlTooltip placement={"right-start"}

                               title={
                                 <React.Fragment>
                                   <Typography variant={"body1"}>How soon should the system retry the failed step after encountering a failure.
                                     Subsequent retries will also be at this value if the backoff coefficient is set to 1 or less.</Typography>
                                 </React.Fragment>
                               }>
                    <IconButton>
                      <HelpRoundedIcon/>
                    </IconButton>
                  </HtmlTooltip>
                </Box>
                <Box>
                  <TextField label="Initial Interval" type="number" variant="outlined" margin={"normal"}
                             value={rootStore.preferencesStore.newInitialInterval}
                             onChange={(event) => rootStore.preferencesStore.setInitialInterval(event.target.value)}/>
                  <TimeSelection key={"initial-interval-time"}
                                 value={rootStore.preferencesStore.newInitialIntervalTime} onChange={(event) => {
                    rootStore.preferencesStore.setInitialIntervalTime(event.target.value)
                  }}/>
                </Box>
              </Grid>

              <Grid xs={12}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"}>Maximum Interval</Typography>
                  <HtmlTooltip placement={"right-start"}
                               title={
                                 <React.Fragment>
                                   <Typography variant={"body1"}>The maximum amount of time between a retried communication. Set this value to set
                                     a limit to the exponential backoff coefficient, setting this to 0 means no limit.</Typography>
                                 </React.Fragment>
                               }>
                    <IconButton>
                      <HelpRoundedIcon/>
                    </IconButton>
                  </HtmlTooltip>
                </Box>
                <Box>
                  <TextField label="Maximum Interval" type="number" variant="outlined" margin={"normal"}
                             value={rootStore.preferencesStore.newStartToCloseTimeout}
                             onChange={(event) => rootStore.preferencesStore.setStartToCloseTimeout(event.target.value)}/>
                  <TimeSelection key={"maximum-interval-time"}
                                 value={rootStore.preferencesStore.newStartToCloseTimeoutTime} onChange={(event) => {
                    rootStore.preferencesStore.setStartToCloseTimeoutTime(event.target.value)
                  }}/>
                  <Button variant={rootStore.preferencesStore.newStartToCloseTimeout == 0 ? "contained" : "outlined"}
                          color={rootStore.preferencesStore.newStartToCloseTimeout == 0 ? "secondary" : "info"}
                          onClick={() => rootStore.preferencesStore.setStartToCloseTimeout("0")}
                          endIcon={<NotInterestedRoundedIcon/>}
                          sx={{height: "56px", mt: 2, ml: 3}}>No Limit</Button>
                </Box>
              </Grid>

              <Grid xs={12}>
                <Box>
                  <Typography variant={"h4"} display={"inline-block"}>Start To Close Timeout</Typography>
                  <HtmlTooltip placement={"right-start"}
                               title={
                                 <React.Fragment>
                                   <Typography variant={"body1"}>The maximum amount of time it should take to send a communication, if the communication execution exceeds this limit then there are no more retries. Set to 0 to disable</Typography>
                                 </React.Fragment>
                               }>
                    <IconButton>
                      <HelpRoundedIcon/>
                    </IconButton>
                  </HtmlTooltip>
                </Box>
                <Box>
                  <TextField label="Maximum Interval" type="number" variant="outlined" margin={"normal"}
                             value={rootStore.preferencesStore.newMaximumInterval}
                             onChange={(event) => rootStore.preferencesStore.setMaximumInterval(event.target.value)}/>
                  <TimeSelection key={"maximum-interval-time"}
                                 value={rootStore.preferencesStore.newMaximumIntervalTime} onChange={(event) => {
                    rootStore.preferencesStore.setMaximumIntervalTime(event.target.value)
                  }}/>
                  <Button variant={rootStore.preferencesStore.newMaximumInterval == 0 ? "contained" : "outlined"}
                          color={rootStore.preferencesStore.newMaximumInterval == 0 ? "secondary" : "info"}
                          onClick={() => rootStore.preferencesStore.setMaximumInterval("0")}
                          endIcon={<NotInterestedRoundedIcon/>}
                          sx={{height: "56px", mt: 2, ml: 3}}>No Limit</Button>
                </Box>
              </Grid>
            </>
          )
        }
        <Grid xs={12}>
          <Button variant="contained" color={"primary"} endIcon={<SaveRoundedIcon/>} size={"large"}>Save Changes</Button>
        </Grid>
      </Grid>
    </>
  )
})

export default Preferences;
