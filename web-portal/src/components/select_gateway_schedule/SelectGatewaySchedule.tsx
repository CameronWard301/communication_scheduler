import {observer} from "mobx-react-lite";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, Typography, useTheme} from "@mui/material";
import CustomTooltip from "../tooltip";
import TextFieldFilter from "../text_field_filter";
import TagRoundedIcon from "@mui/icons-material/TagRounded";
import SearchRoundedIcon from "@mui/icons-material/SearchRounded";
import WebhookRoundedIcon from "@mui/icons-material/WebhookRounded";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import LoadingButton from "@mui/lab/LoadingButton";
import RefreshRoundedIcon from "@mui/icons-material/RefreshRounded";
import {DataGrid, GridRowSelectionModel} from "@mui/x-data-grid";
import {useStore} from "../../context/StoreContext.tsx";
import {useGatewayService} from "../../service/GatewayService.ts";
import useGatewayGridDef from "../gateway_table/useGatewayGridDef.tsx";
import {Gateway} from "../../models/Gateways.ts";
import {useEffect} from "react";

export interface SelectGatewayScheduleProps {
  selectionModel: GridRowSelectionModel
  setSelectionModel: (selectionModel: GridRowSelectionModel) => void;
  setSelectedGateway: (gateway: Gateway) => void;
}

const SelectGatewaySchedule = observer(({
                                          selectionModel,
                                          setSelectionModel,
                                          setSelectedGateway
                                        }: SelectGatewayScheduleProps) => {
  const theme = useTheme()
  const rootStore = useStore();
  const {getGatewaysForTable} = useGatewayService();
  const {gatewayFilterColumns} = useGatewayGridDef();

  useEffect(() => {
    getGatewaysForTable();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.gatewayTableStore.paginationModel, rootStore.gatewayTableStore.sortModel]);

  return (
    <>
      <Grid xs={12}>
        <Typography variant={"h4"} display={"inline-block"} id={"select-gateway-title"}>Select Gateway<span
          style={{color: theme.palette.error.main}}>*</span></Typography>
        <CustomTooltip message="Select the gateway to process the message" ariaLabelTopic={"Select gateway"}/>
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
                       id={"refresh-gateways-button"}
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
                  rowSelectionModel={selectionModel}
                  onRowSelectionModelChange={(selectionModel) => {
                    setSelectionModel(selectionModel);
                    setSelectedGateway(rootStore.gatewayTableStore.gatewayTableData[rootStore.gatewayTableStore.gatewayTableData.findIndex((gateway) => gateway.id === selectionModel[0])])
                  }}
        />
      </Grid>
    </>
  )
})

export default SelectGatewaySchedule;
