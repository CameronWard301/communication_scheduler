// Adapted from: https://mui.com/material-ui/react-stepper/


import {styled} from "@mui/material/styles";

export const StepIconRoot = styled("div")<{ ownerState: { active?: boolean } }>(({theme, ownerState}) => ({
  color: theme.palette.mode === "dark" ? theme.palette.grey[700] : "#eaeaf0",
  display: "flex",
  height: 22,
  alignItems: "center",
  ...(ownerState.active && {
    color: theme.palette.success.main,
  }),
  "& .StepIcon-completedIcon": {
    color: theme.palette.primary.main,
    zIndex: 1,
    fontSize: 22,
  },
  "& .StepIcon-circle": {
    width: 8,
    height: 8,
    borderRadius: "50%",
    backgroundColor: "currentColor",
  },
}));
