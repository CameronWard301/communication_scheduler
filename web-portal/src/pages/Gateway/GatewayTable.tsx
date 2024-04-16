//Adapted from: https://mui.com/x/react-data-grid/
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, Typography} from "@mui/material";
import {DataGrid} from '@mui/x-data-grid';
import {observer} from "mobx-react-lite";
import {useStore} from "../../context/StoreContext.tsx";
import useGatewayGridDef from "../../components/gateway_table/useGatewayGridDef.tsx";
import AddCircleOutlineRoundedIcon from '@mui/icons-material/AddCircleOutlineRounded';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import WebhookRoundedIcon from '@mui/icons-material/WebhookRounded';
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded';
import DeleteRounded from '@mui/icons-material/DeleteRounded';
import {useEffect} from "react";
import {useGatewayService} from "../../service/GatewayService.ts";
import TagRoundedIcon from '@mui/icons-material/TagRounded';
import TextFieldFilter from "../../components/text_field_filter";
import LoadingButton from "@mui/lab/LoadingButton";
import {GatewayModal} from "../../components/modal";
import {ReviewGatewayTable} from "../../components/modal/gateway";
import {useNavigate, useSearchParams} from "react-router-dom";
import useQuery from "../../helper/UseQuery.ts";

const GatewayTable = observer(() => {
  const rootStore = useStore();
  const {gatewayTablePageColumns} = useGatewayGridDef();
  const {getGatewaysForTable, deleteGatewayById} = useGatewayService();
  const navigate = useNavigate();
  const [, setSearchParams] = useSearchParams();


  const query = useQuery();
  useEffect(() => {
    const gatewayId = query.get("gatewayId");
    const gatewayName = query.get("gatewayName");
    const gatewayDescription = query.get("gatewayDescription");
    const gatewayEndpointUrl = query.get("gatewayEndpointUrl");
    if (gatewayId !== null) {
      rootStore.gatewayTableStore.setGatewayIdFilter(gatewayId);
    }
    if (gatewayName !== null) {
      rootStore.gatewayTableStore.setGatewayNameFilter(gatewayName);
    }
    if (gatewayDescription !== null) {
      rootStore.gatewayTableStore.setGatewayDescriptionFilter(gatewayDescription);
    }
    if (gatewayEndpointUrl !== null) {
      rootStore.gatewayTableStore.setGatewayEndpointUrlFilter(gatewayEndpointUrl);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    getGatewaysForTable();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.gatewayTableStore.paginationModel, rootStore.gatewayTableStore.sortModel]);

  return (
    <>
      <GatewayModal confirmIcon={<DeleteRounded/>}
                    gateway={rootStore.gatewayTableStore.selectedGateway}
                    open={rootStore.gatewayTableStore.deleteModalOpen}
                    setOpen={rootStore.gatewayTableStore.setDeleteModalOpen}
                    affectedSchedules={rootStore.gatewayTableStore.affectedSchedules}
                    isLoading={rootStore.gatewayTableStore.isLoading}
                    heading={"Delete Gateway"}
                    confirmText={"Delete"}
                    onConfirm={() => {
                      deleteGatewayById(rootStore.gatewayTableStore.selectedGateway);
                    }}
                    affectedScheduleTooltip={"Deleting this gateway will cause the number of schedules shown to stop working, its best to update the schedules to use a new gateway first by viewing the affected schedules and performing a bulk action."}
                    description={<ReviewGatewayTable gateway={rootStore.gatewayTableStore.selectedGateway}/>}
      />
      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12} mb={2}>
          <Typography variant="h1" fontSize={"4rem"} id={"gateway-page-heading"}>Communication Gateways</Typography>
        </Grid>

        <Grid xs={12} container>
          <Grid xsOffset={6} mdOffset={9} xs={12} mb={0}>
            <Button variant="contained" fullWidth color="primary" endIcon={<AddCircleOutlineRoundedIcon/>}
                    id={"add-gateway-button"} onClick={() => navigate("/add-gateway")}>Add
              Gateway</Button>
          </Grid>
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


        <Grid mdOffset={9} xs={12} container spacing={0} mb={0}>
          <Grid xs={6} sx={{pr: 1}}>
            <Button variant="contained" fullWidth color="info" endIcon={<CloseRoundedIcon/>}
                    onClick={() => {
                      rootStore.gatewayTableStore.resetFilters();
                      setSearchParams(new URLSearchParams());
                      getGatewaysForTable();
                    }}
                    disabled={rootStore.gatewayTableStore.gatewayIdFilter === "" && rootStore.gatewayTableStore.gatewayNameFilter === "" && rootStore.gatewayTableStore.gatewayDescriptionFilter === "" && rootStore.gatewayTableStore.gatewayEndpointUrlFilter === ""}
                    id={"reset-filters-button"}
            >Reset Filters</Button>
          </Grid>
          <Grid xs={6} sx={{pl: 1}}>
            <LoadingButton loading={rootStore.gatewayTableStore.isLoading} variant="contained" fullWidth
                           color="secondary" endIcon={<RefreshRoundedIcon/>}
                           id={"refresh-gateways-button"}
                           onClick={() => getGatewaysForTable()} loadingPosition="end"
            >Refresh</LoadingButton>
          </Grid>
        </Grid>

        <Grid xs={12}>
          <DataGrid columns={gatewayTablePageColumns} rows={rootStore.gatewayTableStore.gatewayTableData}
                    autoHeight
                    initialState={{
                      pagination: {
                        paginationModel: rootStore.gatewayTableStore.paginationModel
                        ,
                      },
                    }}
                    aria-label={"gateway-table"}
                    disableRowSelectionOnClick
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
          />
        </Grid>

      </Grid>
    </>
  )
})

export default GatewayTable
