//Adapted from: https://mui.com/x/react-data-grid/
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, Typography} from "@mui/material";
import {DataGrid} from '@mui/x-data-grid';
import {observer} from "mobx-react-lite";
import {useStore} from "../context/StoreContext.tsx";
import useGatewayGridDef from "../components/gateway_table/useGatewayGridDef.tsx";
import AddCircleOutlineRoundedIcon from '@mui/icons-material/AddCircleOutlineRounded';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import WebhookRoundedIcon from '@mui/icons-material/WebhookRounded';
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded';
import {useEffect} from "react";
import {useGatewayService} from "../service/GatewayService.ts";
import TagRoundedIcon from '@mui/icons-material/TagRounded';
import TextFieldFilter from "../components/text_field_filter";
import LoadingButton from "@mui/lab/LoadingButton";

const Gateways = observer(() => {
  const rootStore = useStore();
  const {columns} = useGatewayGridDef();
  const {getGateways} = useGatewayService();

  useEffect(() => {
    getGateways();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.gatewayTableStore.paginationModel, rootStore.gatewayTableStore.sortModel]);

  return (
    <>
      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"}
            width={"100%"}>
        <Grid xs={12} mb={2}>
          <Typography variant="h1" fontSize={"4rem"} id={"preferences-page-heading"}>Communication Gateways</Typography>
        </Grid>

        <Grid xs={12} container>
          <Grid xsOffset={6} mdOffset={9} xs={12} mb={0}>
            <Button variant="contained" fullWidth color="primary" endIcon={<AddCircleOutlineRoundedIcon/>}>Add
              Gateway</Button>
          </Grid>
        </Grid>

        <Grid xs={3}>
          <TextFieldFilter fieldValue={rootStore.gatewayTableStore.gatewayIdFilter}
                           setFieldValue={rootStore.gatewayTableStore.setGatewayIdFilter}
                           isFieldFocused={rootStore.gatewayTableStore.gatewayIdFocus}
                           setIsFieldFocused={rootStore.gatewayTableStore.setGatewayIdFocus}
                           idPrefix={"gateway-id"}
                           label={"Gateway ID"}
                           InputIcon={TagRoundedIcon}
                           fetchResults={getGateways}
          />
        </Grid>

        <Grid xs={3}>
          <TextFieldFilter fieldValue={rootStore.gatewayTableStore.gatewayNameFilter}
                           setFieldValue={rootStore.gatewayTableStore.setGatewayNameFilter}
                           isFieldFocused={rootStore.gatewayTableStore.gatewayNameFocus}
                           setIsFieldFocused={rootStore.gatewayTableStore.setGatewayNameFocus}
                           idPrefix={"gateway-name"}
                           label={"Gateway Name"}
                           InputIcon={SearchRoundedIcon}
                           fetchResults={getGateways}
          />
        </Grid>

        <Grid xs={3}>
          <TextFieldFilter fieldValue={rootStore.gatewayTableStore.gatewayDescriptionFilter}
                           setFieldValue={rootStore.gatewayTableStore.setGatewayDescriptionFilter}
                           isFieldFocused={rootStore.gatewayTableStore.gatewayDescriptionFocus}
                           setIsFieldFocused={rootStore.gatewayTableStore.setGatewayDescriptionFocus}
                           idPrefix={"gateway-description"}
                           label={"Description"}
                           InputIcon={SearchRoundedIcon}
                           fetchResults={getGateways}
          />
        </Grid>

        <Grid xs={3}>
          <TextFieldFilter fieldValue={rootStore.gatewayTableStore.gatewayEndpointUrlFilter}
                           setFieldValue={rootStore.gatewayTableStore.setGatewayEndpointUrlFilter}
                           isFieldFocused={rootStore.gatewayTableStore.gatewayEndpointUrlFocus}
                           setIsFieldFocused={rootStore.gatewayTableStore.setGatewayEndpointUrlFocus}
                           idPrefix={"gateway-url"}
                           label={"Endpoint URL"}
                           InputIcon={WebhookRoundedIcon}
                           fetchResults={getGateways}
          />
        </Grid>


        <Grid mdOffset={9} xs={12} container spacing={0} mb={0}>
          <Grid xs={6} sx={{pr: 1}}>
            <Button variant="contained" fullWidth color="info" endIcon={<CloseRoundedIcon/>}
                    onClick={() => {
                      rootStore.gatewayTableStore.resetFilters();
                      getGateways();
                    }}
            >Reset Filters</Button>
          </Grid>
          <Grid xs={6} sx={{pl: 1}}>
            <LoadingButton loading={rootStore.gatewayTableStore.isLoading} variant="contained" fullWidth color="secondary" endIcon={<RefreshRoundedIcon/>}
                           onClick={() => getGateways()} loadingPosition="end"
            >Refresh</LoadingButton>
          </Grid>
        </Grid>

        <Grid xs={12}>
          <DataGrid columns={columns} rows={rootStore.gatewayTableStore.gatewayTableData}
                    autoHeight
                    initialState={{
                      pagination: {
                        paginationModel: rootStore.gatewayTableStore.paginationModel
                        ,
                      },
                    }}
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

export default Gateways
