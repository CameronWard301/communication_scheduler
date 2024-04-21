// Adapted from: https://mui.com/material-ui/react-stepper/
import {StepIconProps} from "@mui/material";
import {Check} from "@mui/icons-material";
import {StepIconRoot} from "./StepIconRoot";

export const StepIcon = (props: StepIconProps) => {
  const {active, completed, className} = props;

  return (
    <StepIconRoot ownerState={{active}} className={className}>
      {completed ? <Check className="StepIcon-completedIcon"/> : <div className="StepIcon-circle"/>}
    </StepIconRoot>
  );
};
