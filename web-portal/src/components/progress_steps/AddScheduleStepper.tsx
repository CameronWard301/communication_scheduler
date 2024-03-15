import {observer} from "mobx-react-lite";
import {ProgressButtons} from "./ProgressButtons.tsx";
import {useStore} from "../../context/StoreContext.tsx";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, Step, StepLabel, Stepper} from "@mui/material";
import {Connector} from "./Connector.tsx";
import React, {useEffect} from "react";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import {StepIcon} from "./StepIcon.tsx";
import {ScheduleType} from "../../models/Schedules.ts";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import {useNavigate} from "react-router-dom";

export interface AddScheduleStepperProps {
  children: React.ReactNode;
  ConfirmButton: React.ReactNode;
}

const steps = ["Details", "Gateway", "Schedule", "Review"];

const AddScheduleStepper = observer(({children, ConfirmButton}: AddScheduleStepperProps) => {
  const rootStore = useStore();
  const navigate = useNavigate();

  useEffect(() => {
    switch (rootStore.scheduleAddStore.currentStep) {
      case 0:
        if (!rootStore.scheduleAddStore.isUserIdValid()) {
          rootStore.scheduleAddStore.setAllowNext(false);
          return;
        }
        break;
      case 1:
        if (rootStore.scheduleAddStore.gatewaySelectionModel.length === 0) {
          rootStore.scheduleAddStore.setAllowNext(false);
          return;
        }
        break;
      case 2:
        if (rootStore.scheduleAddStore.selectedScheduleType === ScheduleType.Interval && !rootStore.createScheduleStore.isIntervalFieldsValid()) {
          rootStore.scheduleAddStore.setAllowNext(false);
          return;
        }
        if (rootStore.scheduleAddStore.selectedScheduleType === ScheduleType.Interval && rootStore.createScheduleStore.isIntervalFieldsAllZero()) {
          rootStore.scheduleAddStore.setAllowNext(false);
          return;
        }
    }
    rootStore.scheduleAddStore.setAllowNext(true);
  }, [rootStore.scheduleAddStore.currentStep, rootStore.scheduleAddStore.userId, rootStore.scheduleAddStore.gatewaySelectionModel, rootStore.scheduleAddStore.selectedScheduleType, rootStore.createScheduleStore.intervalOffset, rootStore.createScheduleStore.intervalSeconds, rootStore.createScheduleStore.intervalMinutes, rootStore.createScheduleStore.intervalHours, rootStore.createScheduleStore.intervalDays, rootStore.scheduleAddStore, rootStore.createScheduleStore]
  );

  return (
    <>
      <Grid xs={12} sx={{display: {xs: "none", sm: "flex"}}}>
        <ProgressButtons allowNext={rootStore.scheduleAddStore.allowNext}
                         currentStep={rootStore.scheduleAddStore.currentStep}
                         nextStep={rootStore.scheduleAddStore.incrementStep}
                         previousStep={rootStore.scheduleAddStore.decrementStep}
                         maxStep={steps.length}>


          <Stepper sx={{width: '100%'}} alternativeLabel activeStep={rootStore.scheduleAddStore.currentStep}
                   connector={<Connector/>}>
            {steps.map((step) => (
              <Step key={step}>
                <StepLabel StepIconComponent={StepIcon}>{step}</StepLabel>
              </Step>
            ))}
          </Stepper>

        </ProgressButtons>
      </Grid>
      {children}
      <Grid xs={12}>
        {
          rootStore.scheduleAddStore.currentStep < steps.length - 1 && (
            <Button sx={{width: "300px", my: 1}} variant="contained" endIcon={<ArrowForwardIcon/>}
                    disabled={!rootStore.scheduleAddStore.allowNext || rootStore.scheduleAddStore.currentStep == steps.length}
                    color="primary" onClick={() => rootStore.scheduleAddStore.incrementStep()}>
              Next
            </Button>
          )
        }
        {
          rootStore.scheduleAddStore.currentStep === steps.length - 1 && (
            <>
              <Button variant={"contained"} color={"info"} endIcon={<CloseRoundedIcon/>} onClick={() => navigate("/schedules")}>Cancel</Button>
              {ConfirmButton}
            </>

          )
        }

      </Grid>

    </>
  )
})

export default AddScheduleStepper;
