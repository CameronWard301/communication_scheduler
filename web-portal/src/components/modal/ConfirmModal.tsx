import React from "react";
import {Button, Fade, Modal, Typography, useTheme} from "@mui/material";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Unstable_Grid2";
import CloseIcon from "@mui/icons-material/Close";
import {observer} from "mobx-react-lite";
import LoadingButton from "@mui/lab/LoadingButton";

type Props = {
  open: boolean;
  setOpen: (show: boolean) => void;
  heading: React.ReactNode;
  description: React.ReactNode;
  confirmIcon: React.ReactNode;
  confirmText: string;
  cancelIcon?: React.ReactNode;
  cancelText?: string;
  onConfirm: () => void;
  loading: boolean;
};

const ConfirmModal = observer(
  ({
     open,
     setOpen,
     heading,
     description,
     confirmIcon,
     confirmText,
     cancelIcon = <CloseIcon/>,
     cancelText = "Cancel",
     onConfirm,
     loading
   }: Props) => {
    const theme = useTheme();
    const style = {
      maxWidth: "80%",
      maxHeight: "93vh",
      backgroundColor: "background.paper",
      border: `2px solid ${theme.palette.primary.main}`,
      borderRadius: 2,
      boxShadow: 24,
      p: 4,
      overflowY: "auto",
    };

    return (
      <Modal
        aria-labelledby="transition-modal-title"
        aria-describedby="transition-modal-description"
        open={open}
        onClose={() => {
          setOpen(false);
        }}
        closeAfterTransition
      >
        <Fade in={open}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              minHeight: "100vh",
            }}
          >
            <Box sx={style}>
              <Grid container spacing={2}>
                <Grid xs={12}>
                  <Typography id="transition-modal-title" variant="h6" component="h2">
                    {heading}
                  </Typography>
                </Grid>
                <Grid xs={12}>
                  <Box id="transition-modal-description">{description}</Box>
                </Grid>

                <Grid xs={12} md={6}>
                  <Button
                    variant="contained"
                    endIcon={cancelIcon}
                    id={"cancel-modal-button"}
                    color={"info"}
                    fullWidth
                    sx={{height: 56}}
                    onClick={() => {
                      setOpen(false);
                    }}
                  >
                    {cancelText}
                  </Button>
                </Grid>
                <Grid xs={12} md={6} marginTop={"auto"}>
                  <LoadingButton loading={loading} onClick={onConfirm} endIcon={confirmIcon} variant="contained"
                                 fullWidth sx={{height: 56}}
                                 color={"primary"} id={"confirm-modal-button"}>
                    <span>{confirmText}</span>
                  </LoadingButton>
                </Grid>
              </Grid>
            </Box>
          </Box>
        </Fade>
      </Modal>
    );
  }
);

export default ConfirmModal;
