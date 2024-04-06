import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import {Button} from "@mui/material";
import {observer} from "mobx-react-lite";
import React from "react";

export interface ProgressButtonsProps {
  allowNext: boolean;
  currentStep: number;
  maxStep: number;
  nextStep: () => void;
  previousStep: () => void;
  children?: React.ReactNode;
}

export const ProgressButtons = observer(({
                                           allowNext,
                                           maxStep,
                                           nextStep,
                                           previousStep,
                                           currentStep,
                                           children
                                         }: ProgressButtonsProps) => {

  return (
    <>
      <Button
        variant="contained"
        startIcon={<ArrowBackIcon/>}
        color="info"
        onClick={() => {
          previousStep();
        }}
        id={"back-step"}
        disabled={currentStep === 0}
        sx={{width: "300px", my: 1}}
      >
        Previous
      </Button>
      {children}

      <Button sx={{width: "300px", my: 1}} variant="contained" endIcon={<ArrowForwardIcon/>}
              id={"next-step"}
              disabled={!allowNext || currentStep == maxStep - 1} color="primary" onClick={() => nextStep()}>
        Next
      </Button>
    </>

  );
});
