import {OverridableComponent} from "@mui/material/OverridableComponent";
import {Badge, Button, InputAdornment, Paper, SvgIconTypeMap, TextField} from "@mui/material";
import {observer} from "mobx-react-lite";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import Box from "@mui/material/Box";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import DoneRoundedIcon from "@mui/icons-material/DoneRounded";
import {useSearchParams} from "react-router-dom";
import ArrowDropDownRoundedIcon from '@mui/icons-material/ArrowDropDownRounded';
import ArrowDropUpRoundedIcon from '@mui/icons-material/ArrowDropUpRounded';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import {useStore} from "../../context/StoreContext.tsx";
import LoadingButton from "@mui/lab/LoadingButton";
import {DataGrid} from "@mui/x-data-grid";
import useGatewayGridDef from "../gateway_table/useGatewayGridDef.tsx";
import {useEffect} from "react";
import {useGatewayService} from "../../service/GatewayService.ts";

export interface GatewayFilterProps {
  fieldValue: string;
  setFieldValue: (value: string) => void;

  isFieldFocused: boolean;
  setIsFieldFocused: (value: boolean) => void;

  idPrefix: string;
  queryParam: string;
  label: string;

  fetchResults: () => void;

  InputIcon: OverridableComponent<SvgIconTypeMap>;
}

const GatewayFilter = observer(({
                                  isFieldFocused,
                                  setIsFieldFocused,
                                  setFieldValue,
                                  fieldValue,
                                  InputIcon,
                                  label,
                                  idPrefix,
                                  queryParam,
                                  fetchResults
                                }: GatewayFilterProps) => {

  const [searchParams, setSearchParams] = useSearchParams();
  const rootStore = useStore();
  const {gatewayFilterColumns} = useGatewayGridDef();
  const {getGatewaysForScheduleFilter} = useGatewayService();

  const setTextField = (value: string, queryParam: string) => {
    setFieldValue(value)
    //getPrevious search params and update the query param
    const params = new URLSearchParams(searchParams);
    if (value === "") {
      params.delete(queryParam);
    } else {
      params.set(queryParam, value);
    }
    setSearchParams(params);
  }

  useEffect(() => {
    getGatewaysForScheduleFilter();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rootStore.gatewayFilterStore.paginationModel, rootStore.gatewayFilterStore.sortModel, rootStore.gatewayFilterStore.gatewayIdFilter, rootStore.gatewayFilterStore.gatewayNameFilter, rootStore.gatewayFilterStore.gatewayDescriptionFilter, rootStore.gatewayFilterStore.gatewayEndpointUrlFilter]);

  return (
    <Grid container direction={"column"} position={"relative"} className={"gatewayFilter"} zIndex={50}>
      <Grid xs={12} className={"gatewayFilter"}>
        <Box sx={{alignItems: 'center'}} className={"gatewayFilter"}>
          <Button variant={"outlined"}
                  startIcon={InputIcon && (
                    <Badge color={"primary"} variant={"dot"} invisible={fieldValue == ""}>
                      <InputIcon sx={{color: 'action.active', mr: -0.5, my: -0.5, width: '35px', height: '35px'}}
                                 fontSize="large"/>
                    </Badge>

                  )}
                  id={`${idPrefix}-filter-button`}
                  fullWidth
                  size={"large"}
                  endIcon={isFieldFocused ? <ArrowDropUpRoundedIcon sx={{justifyContent: "flex-end"}}/> :
                    <ArrowDropDownRoundedIcon/>}
                  color={"info"}
                  className={"gatewayFilter"}
                  onClick={() => setIsFieldFocused(!isFieldFocused)}
                  sx={{
                    height: '56px', display: 'flex', justifyContent: "flex-start",
                    width: "100%",
                    "& .MuiButton-endIcon": {
                      position: "absolute",
                      right: "1rem",
                    }
                  }}

          ><span style={{paddingLeft: 10}}>Gateway</span></Button>

        </Box>
      </Grid>
      {
        isFieldFocused && (
          <Grid xs={3} sx={{
            position: "absolute",
            mt: "56px",
            zIndex: 1,
            width: "100%",

          }}>
            <Paper sx={{p: 2, mb: 2}}>
              <Grid container spacing={2}>
                <Grid xs={12} md={6}>
                  <Button id={`${idPrefix}-filter-reset-button`} aria-haspopup="true"
                          aria-controls={`${idPrefix}-filter-menu-reset`}
                          aria-label={`${label} Filter Reset`} variant="contained" color="info" size="large"
                          endIcon={<CloseRoundedIcon/>} fullWidth onClick={() => {
                    setIsFieldFocused(false);
                    setTextField("", queryParam)
                    rootStore.gatewayFilterStore.resetFilters();
                    fetchResults();
                  }}>
                    Reset Filter
                  </Button>
                </Grid>
                <Grid xs={12} md={6}>
                  <Button id={`${idPrefix}-filter-apply-button`} aria-haspopup="true"
                          aria-controls={`${idPrefix}-filter-menu-apply`}
                          aria-label={`${label} Filter Apply`} variant="contained" color="primary" size="large"
                          endIcon={<DoneRoundedIcon/>} fullWidth
                          disabled={rootStore.gatewayFilterStore.rowSelectionModel.length === 0}
                          onClick={() => {
                            setTextField(rootStore.gatewayFilterStore.rowSelectionModel.length > 0 ? rootStore.gatewayFilterStore.rowSelectionModel[0] as string : "", queryParam);
                            setIsFieldFocused(false)
                          }}
                  >
                    Apply Filter
                  </Button>
                </Grid>
                <Grid xs={12} md={6}>
                  <TextField fullWidth
                             margin={"normal"}
                             value={rootStore.gatewayFilterStore.gatewayIdFilter}
                             id={"gateway-id-search"}
                             variant={"outlined"}
                             label={"Gateway ID"}
                             InputProps={{
                               startAdornment: (
                                 <InputAdornment position="start" sx={{color: 'action.active', mr: 2.5, my: 1}}>
                                   <SearchRoundedIcon sx={{color: 'action.active', mr: -0.5, my: -0.5}} fontSize="large"/>
                                 </InputAdornment>
                               ),
                             }}
                             onChange={(event) => rootStore.gatewayFilterStore.setGatewayIdFilter(event.target.value)}
                  >

                  </TextField>
                </Grid>

                <Grid xs={12} md={6}>
                  <TextField fullWidth
                             margin={"normal"}
                             value={rootStore.gatewayFilterStore.gatewayNameFilter}
                             id={"gateway-name-search"}
                             variant={"outlined"}
                             label={"Gateway Name"}
                             InputProps={{
                               startAdornment: (
                                 <InputAdornment position="start" sx={{color: 'action.active', mr: 2.5, my: 1}}>
                                   <SearchRoundedIcon sx={{color: 'action.active', mr: -0.5, my: -0.5}} fontSize="large"/>
                                 </InputAdornment>
                               ),
                             }}
                             onChange={(event) => rootStore.gatewayFilterStore.setGatewayNameFilter(event.target.value)}
                  >

                  </TextField>
                </Grid>

                <Grid xs={12} md={6}>
                  <TextField fullWidth
                             margin={"normal"}
                             value={rootStore.gatewayFilterStore.gatewayDescriptionFilter}
                             id={"gateway-description-search"}
                             variant={"outlined"}
                             label={"Gateway Description"}
                             InputProps={{
                               startAdornment: (
                                 <InputAdornment position="start" sx={{color: 'action.active', mr: 2.5, my: 1}}>
                                   <SearchRoundedIcon sx={{color: 'action.active', mr: -0.5, my: -0.5}} fontSize="large"/>
                                 </InputAdornment>
                               ),
                             }}
                             onChange={(event) => rootStore.gatewayFilterStore.setGatewayDescriptionFilter(event.target.value)}
                  >

                  </TextField>
                </Grid>

                <Grid xs={12} md={6}>
                  <TextField fullWidth
                             margin={"normal"}
                             value={rootStore.gatewayFilterStore.gatewayEndpointUrlFilter}
                             id={"gateway-url-search"}
                             variant={"outlined"}
                             label={"Gateway URL"}
                             InputProps={{
                               startAdornment: (
                                 <InputAdornment position="start" sx={{color: 'action.active', mr: 2.5, my: 1}}>
                                   <SearchRoundedIcon sx={{color: 'action.active', mr: -0.5, my: -0.5}} fontSize="large"/>
                                 </InputAdornment>
                               ),
                             }}
                             onChange={(event) => rootStore.gatewayFilterStore.setGatewayEndpointUrlFilter(event.target.value)}
                  >

                  </TextField>
                </Grid>

                <Grid xs={12} md={6}>
                  <LoadingButton endIcon={<SearchRoundedIcon/>}
                                 variant={"contained"}
                                 color={"secondary"}
                                 fullWidth
                                 id={"gateway-filter-search"}
                                 loading={rootStore.gatewayFilterStore.isLoading}
                                 onClick={() => fetchResults()}
                  >Find Gateways</LoadingButton>

                </Grid>

                <Grid xs={12}>
                  <DataGrid columns={gatewayFilterColumns} rows={rootStore.gatewayFilterStore.gatewayTableData}
                            autoHeight
                            disableMultipleRowSelection
                            checkboxSelection
                            pageSizeOptions={[5, 10, 25, 50, 100]}
                            initialState={{
                              pagination: {
                                paginationModel: rootStore.gatewayFilterStore.paginationModel,
                              },
                              columns: {
                                columnVisibilityModel: {
                                  dateCreated: false,
                                  endpointUrl: false,
                                }
                              }
                            }}
                            paginationModel={rootStore.gatewayFilterStore.paginationModel}
                            paginationMode={'server'}
                            sortingMode={'server'}
                            rowCount={rootStore.gatewayFilterStore.totalCount}
                            loading={rootStore.gatewayFilterStore.isLoading}
                            onPaginationModelChange={(paginationModel) => {
                              rootStore.gatewayFilterStore.setPaginationModel(paginationModel);
                            }}
                            onSortModelChange={(sortModel) => {
                              rootStore.gatewayFilterStore.setSortModel(sortModel);
                            }}
                            onRowSelectionModelChange={(newRowSelectionModel) => {
                              rootStore.gatewayFilterStore.setRowSelectionModel(newRowSelectionModel);
                            }}
                            rowSelectionModel={rootStore.gatewayFilterStore.rowSelectionModel}

                  >

                  </DataGrid>
                </Grid>

              </Grid>


            </Paper>
          </Grid>
        )
      }

    </Grid>
  )
})

export default GatewayFilter;
