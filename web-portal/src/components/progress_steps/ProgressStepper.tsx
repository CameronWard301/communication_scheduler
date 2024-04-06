import {observer} from "mobx-react-lite";
import React, {useEffect} from "react";
import {GenerateScheduleStore} from "../../stores/GenerateScheduleStore.tsx";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {ProgressButtons} from "./ProgressButtons.tsx";
import {Connector} from "./Connector.tsx";
import {Button, Step, StepLabel, Stepper} from "@mui/material";
import {StepIcon} from "./StepIcon.tsx";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import {BulkActionStore} from "../../stores/BulkActionStore.tsx";

export interface StepperProps {
  children: React.ReactNode;
  confirmButton: React.ReactNode;
  steps: string[];
  store: GenerateScheduleStore | BulkActionStore;
  validateProgress: (store: GenerateScheduleStore | BulkActionStore) => void;
}

const ProgressStepper = observer(({
                                    confirmButton,
                                    steps,
                                    store,
                                    validateProgress,
                                    children
                                  }: StepperProps) => {

  useEffect(() => {
    validateProgress(store);
  }, [store, validateProgress]);

  return (
    <>
      <Grid xs={12} sx={{display: {xs: "none", sm: "flex"}}}>
        <ProgressButtons allowNext={store.allowNext}
                         currentStep={store.currentStep}
                         nextStep={store.incrementStep}
                         previousStep={store.decrementStep}
                         maxStep={steps.length}>


          <Stepper sx={{width: '100%'}} alternativeLabel activeStep={store.currentStep}
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
          store.currentStep < steps.length - 1 && (
            <Button sx={{width: "300px", my: 1}} variant="contained" endIcon={<ArrowForwardIcon/>}
                    disabled={!store.allowNext || store.currentStep == steps.length}
                    color="primary" onClick={() => store.incrementStep()} id={"next-step-bottom"}>
              Next
            </Button>
          )
        }
        {
          store.currentStep === steps.length - 1 && (
            <>
              <Button variant={"contained"} color={"info"} startIcon={<ArrowBackIcon/>}
                      onClick={() => store.decrementStep()}>Previous</Button>
              {confirmButton}
            </>

          )
        }

      </Grid>
    </>
  )
})

export default ProgressStepper
