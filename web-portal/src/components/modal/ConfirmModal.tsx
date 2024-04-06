import React from "react";
import {Button, Fade, Modal, Typography, useTheme} from "@mui/material";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Unstable_Grid2";
import CloseIcon from "@mui/icons-material/Close";
import {observer} from "mobx-react-lite";
import LoadingButton from "@mui/lab/LoadingButton";

export type ConfirmModalProps = {
  open: boolean;
  setOpen: (show: boolean) => void;
  heading: React.ReactNode;
  description: React.ReactNode;
  confirmIcon: React.ReactNode;
  confirmText: string;
  allowConfirm?: boolean;
  confirmErrorMessage?: string;
  cancelIcon?: React.ReactNode;
  cancelText?: string;
  descriptionContainer?: boolean;
  onConfirm: () => void;
  loading: boolean;
  height?: string;
  width?: string;
  fixButtonsBottom?: boolean;
};

const ConfirmModal = observer(
  ({
     open,
     setOpen,
     heading,
     description,
     confirmIcon,
     confirmText,
     allowConfirm = true,
     confirmErrorMessage = "",
     cancelIcon = <CloseIcon/>,
     cancelText = "Cancel",
     onConfirm,
     height,
     width,
     descriptionContainer = true,
     fixButtonsBottom = false,
     loading
   }: ConfirmModalProps) => {
    const theme = useTheme();
    const style = {
      maxWidth: "90%",
      maxHeight: "93vh",
      width: width ? width : "auto",
      height: height ? height : "auto",
      backgroundColor: "background.paper",
      border: `2px solid ${theme.palette.primary.main}`,
      borderRadius: 2,
      boxShadow: 24,
      p: 4,
      display: "flex",
      flexDirection: "column",
      justifyContent: fixButtonsBottom ? "space-between" : "flex-start",
      overflowY: "auto",
      position: "relative"
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
              position: "relative"
            }}
          >
            <Box sx={style}>
              <Grid container spacing={2} sx={{overflowY: "auto"}} alignContent={"flex-start"}
                    pb={fixButtonsBottom ? 3 : 0} mb={fixButtonsBottom ? 2 : 0}>
                <Grid xs={12}>
                  <Typography id="transition-modal-title" variant="h6" component="h2">
                    {heading}
                  </Typography>
                </Grid>
                {descriptionContainer && (
                  <Grid xs={12}>
                    <Box id="transition-modal-description">{description}</Box>
                  </Grid>
                )}
                {!descriptionContainer && (
                  description
                )}

                {confirmErrorMessage && !allowConfirm && (
                  <Grid xs={12}>
                    <Typography variant="body1" color={theme.palette.error.main}>
                      {confirmErrorMessage}
                    </Typography>
                  </Grid>
                )}
                {
                  !fixButtonsBottom && (
                    <>
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
                                       disabled={!allowConfirm}
                                       color={"primary"} id={"confirm-modal-button"}>
                          <span>{confirmText}</span>
                        </LoadingButton>
                      </Grid>
                    </>
                  )
                }


              </Grid>
              {
                fixButtonsBottom && (
                  <Grid container spacing={2}>
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
                                     disabled={!allowConfirm}
                                     color={"primary"} id={"confirm-modal-button"}>
                        <span>{confirmText}</span>
                      </LoadingButton>


                    </Grid>
                  </Grid>

                )
              }

            </Box>
          </Box>
        </Fade>
      </Modal>
    );
  }
);

export default ConfirmModal;
