//Adapted from: https://mui.com/material-ui/react-checkbox/
import FormControl from "@mui/material/FormControl";
import {observer} from "mobx-react-lite";
import {Badge, Button, FormControlLabel, FormGroup, FormHelperText, Paper, useTheme,} from "@mui/material";
import Checkbox from "@mui/material/Checkbox";
import {CommunicationStatus, getStatusColour} from "../../models/History.ts";
import {useStore} from "../../context/StoreContext.tsx";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import ChecklistRoundedIcon from '@mui/icons-material/ChecklistRounded';
import ArrowDropUpRoundedIcon from "@mui/icons-material/ArrowDropUpRounded";
import ArrowDropDownRoundedIcon from "@mui/icons-material/ArrowDropDownRounded";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import {useSearchParams} from "react-router-dom";
import DoneRoundedIcon from "@mui/icons-material/DoneRounded";
import {useEffect, useRef} from "react";

const HistoryStatusSelection = observer(() => {
  const [searchParams, setSearchParams] = useSearchParams();
  const rootStore = useStore();
  const theme = useTheme();
  const elementContainer = useRef<HTMLDivElement>(null);
  const statusButton = useRef<HTMLButtonElement>(null);

  const CommunicationStatusOptions: CommunicationStatus[] = ["Completed", "Running", "Failed", "Terminated", "Cancelled"];

  useEffect(() => {
    const handleFocus = (event: MouseEvent) => {
      if (statusButton.current && statusButton.current.contains(event.target as Node)) {
        rootStore.historyTableStore.setIsStatusFilterFocused(!rootStore.historyTableStore.isStatusFilterFocused)
        return
      }
      if (elementContainer.current && elementContainer.current.contains(event.target as Node)) {
        rootStore.historyTableStore.setIsStatusFilterFocused(true);
      } else {
        rootStore.historyTableStore.setIsStatusFilterFocused(false);
      }
    }
    document.addEventListener('click', handleFocus);
    return () => {
      document.removeEventListener('click', handleFocus);
    }
  })

  useEffect(() => {
    const params = new URLSearchParams(searchParams);
    if (rootStore.historyTableStore.statusFilter.length === 0 || rootStore.historyTableStore.statusFilter[0] === "Any Status") {
      params.delete("status")
    } else {
      params.set("status", rootStore.historyTableStore.statusFilter.toString());
    }
    setSearchParams(params)

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.historyTableStore.statusFilter]);


  return (
    <Grid id={"status-element"} ref={elementContainer} container direction={"column"} position={"relative"}
          className={"gatewayFilter"} zIndex={100}>
      <Grid xs={12} className={"gatewayFilter"}>
        <Box sx={{alignItems: 'center'}} className={"gatewayFilter"} ref={statusButton}>
          <Button variant={"outlined"}
                  startIcon={(
                    <Badge color={"primary"} variant={"dot"}
                           invisible={rootStore.historyTableStore.statusFilter.join() === ["Any Status"].join()}>
                      <ChecklistRoundedIcon
                        sx={{color: 'action.active', mr: -0.5, my: -0.5, width: '35px', height: '35px'}}
                        fontSize="large"/>
                    </Badge>

                  )}
                  id={`status-filter-button`}
                  fullWidth
                  size={"large"}
                  endIcon={rootStore.historyTableStore.isStatusFilterFocused ?
                    <ArrowDropUpRoundedIcon sx={{
                      justifyContent: "flex-end",
                      color: rootStore.platformPreferences.colorTheme == "light" ? theme.palette.info.main : "white"
                    }}/> :
                    <ArrowDropDownRoundedIcon
                      sx={{color: rootStore.platformPreferences.colorTheme == "light" ? theme.palette.info.main : "white"}}/>}
                  color={"info"}
                  className={"gatewayFilter"}

                  sx={{
                    height: '56px', display: 'flex', justifyContent: "flex-start",
                    width: "100%",
                    "& .MuiButton-endIcon": {
                      position: "absolute",
                      right: "1rem",
                    }
                  }}

          ><span style={{
            paddingLeft: 10,
            color: rootStore.platformPreferences.colorTheme == "light" ? theme.palette.info.main : "white"
          }}>Status</span></Button>

        </Box>
      </Grid>
      {
        rootStore.historyTableStore.isStatusFilterFocused && (
          <Grid xs={3} sx={{
            position: "absolute",
            mt: "56px",
            zIndex: 1,
            width: "100%"
          }}>
            <Paper sx={{p: 2, mb: 2}}>
              <Grid container spacing={2}>
                <Grid xs={12}>
                  <Button id={`status-filter-reset-button`} aria-haspopup="true"
                          aria-controls={`status-filter-menu-reset`}
                          aria-label={`Status Filter Reset`} variant="contained" color="info" size="large"
                          endIcon={<CloseRoundedIcon/>} fullWidth onClick={() => {
                    rootStore.historyTableStore.setIsStatusFilterFocused(false);
                    rootStore.historyTableStore.resetFilters();
                  }}>
                    Reset Filter
                  </Button>
                </Grid>
                <Grid xs={12}>
                  <Button id={`status-filter-apply-button`} aria-haspopup="true"
                          aria-controls={`status-filter-menu-apply`}
                          aria-label={`Status Filter Apply`} variant="contained" color="primary" size="large"
                          endIcon={<DoneRoundedIcon/>} fullWidth
                          disabled={rootStore.historyTableStore.isFilterError()}
                          onClick={() => {
                            rootStore.historyTableStore.setIsStatusFilterFocused(false);
                          }}
                  >
                    Apply Filter
                  </Button>
                </Grid>
                <Grid xs={12}>
                  <FormControl variant={"standard"} error={rootStore.historyTableStore.isFilterError()}>
                    <FormGroup>
                      <FormControlLabel
                        control={<Checkbox checked={rootStore.historyTableStore.statusFilter.indexOf("Any Status") > -1}
                                           size={"medium"}
                                           id={"any-status-checkbox"}
                                           onChange={(event) => {
                                             if (event.target.checked) {
                                               rootStore.historyTableStore.setStatusFilter(["Any Status"]);
                                             } else {
                                               rootStore.historyTableStore.removeStatusFromFilter("Any Status");
                                             }
                                           }}/>}
                        label={"Any Status"}
                      />
                      {CommunicationStatusOptions.map((status) => (
                        <FormControlLabel
                          key={status}
                          control={<Checkbox checked={rootStore.historyTableStore.statusFilter.indexOf(status) > -1}
                                             sx={{color: getStatusColour(status, theme) + " !important"}}
                                             size={"medium"}
                                             id={`${status.toLowerCase()}-checkbox`}
                                             onChange={(event) => {
                                               if (event.target.checked) {
                                                 rootStore.historyTableStore.removeStatusFromFilter("Any Status");
                                                 rootStore.historyTableStore.setStatusFilter([status]);
                                               } else {
                                                 rootStore.historyTableStore.removeStatusFromFilter(status);
                                               }
                                             }}/>}
                          label={status}
                          color={"red"}
                          sx={{color: getStatusColour(status, theme)}}
                        />
                      ))}
                      <FormHelperText>{rootStore.historyTableStore.isFilterError() ? "Please choose an option" : ""}</FormHelperText>

                    </FormGroup>
                  </FormControl>
                </Grid>
              </Grid>

            </Paper>
          </Grid>

        )
      }

    </Grid>
  )
})

export default HistoryStatusSelection;
