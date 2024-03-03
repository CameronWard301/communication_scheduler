//Adapted from: https://mui.com/x/react-data-grid/
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, Typography} from "@mui/material";
import {DataGrid} from '@mui/x-data-grid';
import {observer} from "mobx-react-lite";
import {useStore} from "../context/StoreContext.tsx";
import useGatewayGridDef from "../components/gateway_table/useGatewayGridDef.tsx";
import AddCircleOutlineRoundedIcon from '@mui/icons-material/AddCircleOutlineRounded';
import {useEffect} from "react";
import {useGatewayService} from "../service/GatewayService.ts";
import TagRoundedIcon from '@mui/icons-material/TagRounded';
import TextFieldFilter from "../components/text_field_filter";

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
        <Grid xsOffset={6} mdOffset={9} xs={12} mb={2}>
          <Button variant="contained" fullWidth color="primary" endIcon={<AddCircleOutlineRoundedIcon/>}>Add
            Gateway</Button>
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
