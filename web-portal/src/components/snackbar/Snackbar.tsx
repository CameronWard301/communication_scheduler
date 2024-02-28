import React, {Dispatch, SetStateAction} from "react";
import {AlertColor, Snackbar} from "@mui/material";
import MuiAlert, {AlertProps} from "@mui/material/Alert";
import Stack from "@mui/material/Stack";

/**
 * Adapted from: https://mui.com/material-ui/react-snackbar/#consecutive-snackbars
 */
const Alert = React.forwardRef<HTMLDivElement, AlertProps>(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

export type SnackbarProps = {
  snackPack: SnackbarMessage[];
  setSnackPack: Dispatch<SetStateAction<SnackbarMessage[]>>;
};

export type SnackbarMessage = {
  message: string;
  key: number;
  severity: AlertColor;
};

const SnackbarComponent = ({snackPack, setSnackPack}: SnackbarProps) => {
  const [open, setOpen] = React.useState(false);
  const [messageInfo, setMessageInfo] = React.useState<SnackbarMessage | undefined>(undefined);

  React.useEffect(() => {
    if (snackPack.length && !messageInfo) {
      // Set a new snack when we don't have an active one
      setMessageInfo({...snackPack[0]});
      setSnackPack((prev) => prev.slice(1));
      setOpen(true);
    } else if (snackPack.length && messageInfo && open) {
      // Close an active snack when a new one is added
      setOpen(false);
    }
  }, [snackPack, messageInfo, open, setOpen, setSnackPack]);

  const handleClose = (_event?: React.SyntheticEvent | Event, reason?: string) => {
    if (reason === "clickaway") {
      return;
    }

    setOpen(false);
  };

  const handleExited = () => {
    setMessageInfo(undefined);
  };

  return (
    <Stack spacing={2} sx={{width: "100%"}}>
      <Snackbar
        open={open}
        id={"snackbar"}
        autoHideDuration={6000}
        onClose={handleClose}
        anchorOrigin={{vertical: "bottom", horizontal: "right"}}
        key={messageInfo?.key}
        TransitionProps={{onExited: handleExited}}
      >
        <Alert onClose={handleClose} severity={messageInfo?.severity} sx={{width: "100%"}} id={"snackbar-message"}>
          {messageInfo?.message}
        </Alert>
      </Snackbar>
    </Stack>
  );
};
export default SnackbarComponent;
