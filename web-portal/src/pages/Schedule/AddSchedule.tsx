import {observer} from "mobx-react-lite";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, TextField, Typography, useTheme} from "@mui/material";
import ArrowBackRoundedIcon from "@mui/icons-material/ArrowBackRounded";
import {useNavigate} from "react-router-dom";
import {AddScheduleStepper} from "../../components/progress_steps";
import {useStore} from "../../context/StoreContext.tsx";
import Box from "@mui/material/Box";
import CustomTooltip from "../../components/tooltip";
import TextFieldFilter from "../../components/text_field_filter";
import TagRoundedIcon from "@mui/icons-material/TagRounded";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import WebhookRoundedIcon from "@mui/icons-material/WebhookRounded";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import LoadingButton from "@mui/lab/LoadingButton";
import RefreshRoundedIcon from "@mui/icons-material/RefreshRounded";
import {DataGrid} from "@mui/x-data-grid";
import useGatewayGridDef from "../../components/gateway_table/useGatewayGridDef.tsx";
import {useGatewayService} from "../../service/GatewayService.ts";
import {useEffect} from "react";
import {CreateSchedule, CreateScheduleButton} from "../../components/create_schedule";
import {ConfirmGatewayTable} from "../../components/modal/gateway";
import UpcomingCommunications from "../../components/upcoming_communications";

const AddSchedule = observer(() => {
  const navigate = useNavigate();
  const rootStore = useStore();
  const theme = useTheme();
  const {gatewayFilterColumns} = useGatewayGridDef();
  const {getGatewaysForTable} = useGatewayService();

  useEffect(() => {
    getGatewaysForTable();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.gatewayTableStore.paginationModel, rootStore.gatewayTableStore.sortModel]);

  return (
    <>
      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12}>
          <Button sx={{mb: 1}} variant="contained" color="primary" onClick={() => navigate("/schedules")}
                  startIcon={<ArrowBackRoundedIcon/>}>Back to schedules</Button>
          <Typography variant="h1" fontSize={"4rem"} id={"preferences-page-heading"}>Add New Schedule</Typography>
        </Grid>
        <AddScheduleStepper ConfirmButton={<CreateScheduleButton/>}>
          {
            rootStore.scheduleAddStore.currentStep == 0 && (
              <>
                <Grid xs={12} md={6}>
                  <Box>
                    <Typography variant={"h4"} display={"inline-block"} id={"initial-interval-title"}>User ID<span
                      style={{color: theme.palette.error.main}}>*</span></Typography>
                    <CustomTooltip message="The user ID of the user to send the communication to"/>
                  </Box>
                  <Box>
                    <TextField label="UserId" type="text" variant="outlined" margin={"normal"}
                               fullWidth
                               required
                               value={rootStore.scheduleAddStore.userId} id={"user-id-input"}
                               error={!rootStore.scheduleAddStore.isUserIdValid()}
                               helperText={!rootStore.scheduleAddStore.isUserIdValid() ? "Please enter a userId" : ""}
                               onChange={(event) => {
                                 rootStore.scheduleAddStore.setUserId(event.target.value)
                               }
                               }/>
                  </Box>
                </Grid>
              </>
            )
          }

          {
            rootStore.scheduleAddStore.currentStep == 1 && (
              <>
                <Grid xs={12}>
                  <Typography variant={"h4"} display={"inline-block"} id={"initial-interval-title"}>Select Gateway<span
                    style={{color: theme.palette.error.main}}>*</span></Typography>
                  <CustomTooltip message="Select the gateway to process the message"/>
                </Grid>
                <Grid xs={3}>
                  <TextFieldFilter fieldValue={rootStore.gatewayTableStore.gatewayIdFilter}
                                   setFieldValue={rootStore.gatewayTableStore.setGatewayIdFilter}
                                   isFieldFocused={rootStore.gatewayTableStore.gatewayIdFocus}
                                   setIsFieldFocused={rootStore.gatewayTableStore.setGatewayIdFocus}
                                   idPrefix={"gateway-id"}
                                   queryParam={"gatewayId"}
                                   label={"Gateway ID"}
                                   InputIcon={TagRoundedIcon}
                                   fetchResults={getGatewaysForTable}
                  />
                </Grid>

                <Grid xs={3}>
                  <TextFieldFilter fieldValue={rootStore.gatewayTableStore.gatewayNameFilter}
                                   setFieldValue={rootStore.gatewayTableStore.setGatewayNameFilter}
                                   isFieldFocused={rootStore.gatewayTableStore.gatewayNameFocus}
                                   setIsFieldFocused={rootStore.gatewayTableStore.setGatewayNameFocus}
                                   idPrefix={"gateway-name"}
                                   queryParam={"gatewayName"}
                                   label={"Gateway Name"}
                                   InputIcon={SearchRoundedIcon}
                                   fetchResults={getGatewaysForTable}
                  />
                </Grid>

                <Grid xs={3}>
                  <TextFieldFilter fieldValue={rootStore.gatewayTableStore.gatewayDescriptionFilter}
                                   setFieldValue={rootStore.gatewayTableStore.setGatewayDescriptionFilter}
                                   isFieldFocused={rootStore.gatewayTableStore.gatewayDescriptionFocus}
                                   setIsFieldFocused={rootStore.gatewayTableStore.setGatewayDescriptionFocus}
                                   idPrefix={"gateway-description"}
                                   queryParam={"gatewayDescription"}
                                   label={"Description"}
                                   InputIcon={SearchRoundedIcon}
                                   fetchResults={getGatewaysForTable}
                  />
                </Grid>

                <Grid xs={3}>
                  <TextFieldFilter fieldValue={rootStore.gatewayTableStore.gatewayEndpointUrlFilter}
                                   setFieldValue={rootStore.gatewayTableStore.setGatewayEndpointUrlFilter}
                                   isFieldFocused={rootStore.gatewayTableStore.gatewayEndpointUrlFocus}
                                   setIsFieldFocused={rootStore.gatewayTableStore.setGatewayEndpointUrlFocus}
                                   idPrefix={"gateway-url"}
                                   queryParam={"gatewayEndpointUrl"}
                                   label={"Endpoint URL"}
                                   InputIcon={WebhookRoundedIcon}
                                   fetchResults={getGatewaysForTable}
                  />
                </Grid>


                <Grid mdOffset={6} xlOffset={9} xs={6} md={3} xl={3 / 2} sx={{pr: 1}}>
                  <Button variant="contained" fullWidth color="info" endIcon={<CloseRoundedIcon/>}
                          onClick={() => {
                            rootStore.gatewayTableStore.resetFilters();
                            getGatewaysForTable();
                          }}
                          id={"reset-filters-button"}
                  >Reset Filters</Button>
                </Grid>
                <Grid xs={6} xl={3 / 2} md={3} sx={{pl: 1}}>
                  <LoadingButton loading={rootStore.gatewayTableStore.isLoading} variant="contained" fullWidth
                                 color="secondary" endIcon={<RefreshRoundedIcon/>}
                                 onClick={() => getGatewaysForTable()} loadingPosition="end"
                  >Refresh</LoadingButton>
                </Grid>


                <Grid xs={12}>
                  <DataGrid columns={gatewayFilterColumns} rows={rootStore.gatewayTableStore.gatewayTableData}
                            autoHeight
                            initialState={{
                              pagination: {
                                paginationModel: rootStore.gatewayTableStore.paginationModel
                                ,
                              },
                            }}
                            disableMultipleRowSelection
                            checkboxSelection
                            pageSizeOptions={[5, 10, 25, 50, 100]}
                            paginationModel={rootStore.gatewayTableStore.paginationModel}
                            paginationMode={'server'}
                            sortingMode={'server'}
                            rowCount={rootStore.gatewayTableStore.totalCount}
                            loading={rootStore.gatewayTableStore.isLoading}
                            onPaginationModelChange={(paginationModel) => {
                              rootStore.gatewayTableStore.setPaginationModel(paginationModel);
                            }}
                            onSortModelChange={(sortModel) => {
                              rootStore.gatewayTableStore.setSortModel(sortModel);
                            }}
                            rowSelectionModel={rootStore.scheduleAddStore.gatewaySelectionModel}
                            onRowSelectionModelChange={(selectionModel) => {
                              rootStore.scheduleAddStore.setSelectedGateway(rootStore.gatewayTableStore.gatewayTableData[rootStore.gatewayTableStore.gatewayTableData.findIndex((gateway) => gateway.id === selectionModel[0])]);
                              rootStore.scheduleAddStore.setGatewaySelectionModel(selectionModel);
                            }}
                  />
                </Grid>
              </>
            )
          }
          {
            rootStore.scheduleAddStore.currentStep == 2 && (
              <CreateSchedule selectedScheduleType={rootStore.scheduleAddStore.selectedScheduleType}
                              setSelectedScheduleType={rootStore.scheduleAddStore.setSelectedScheduleType}
                              setIntervalSpec={rootStore.scheduleAddStore.setIntervalSpec}
              />
            )
          }

          {
            rootStore.scheduleAddStore.currentStep == 3 && (
              <>
                <Grid xs={12}>
                  <Typography variant={"h4"} display={"inline-block"} id={"review-title"}>Review Details:</Typography>
                </Grid>
                <Grid xs={12}>
                  <Typography variant={"h5"} display={"inline-block"} id={"user-id"}><strong>User
                    ID:</strong> {rootStore.scheduleAddStore.userId}</Typography>
                </Grid>
                <Grid xs={12} md={7} lg={5} xl={4}>
                  <Typography variant={"h5"} display={"inline-block"} id={"gateway-title"}
                              mb={1}><strong>Gateway:</strong></Typography>
                  <ConfirmGatewayTable gateway={rootStore.scheduleAddStore.selectedGateway}></ConfirmGatewayTable>
                </Grid>
                <Grid xs={12}>
                  <UpcomingCommunications scheduleType={rootStore.scheduleAddStore.selectedScheduleType} intervalSpec={rootStore.scheduleAddStore.intervalSpec}/>
                </Grid>

              </>


            )
          }
        </AddScheduleStepper>
      </Grid>
    </>
  )
});

export default AddSchedule;
