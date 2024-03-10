import Grid from "@mui/material/Unstable_Grid2/Grid2";
import Box from "@mui/material/Box";
import {Badge, Button, InputAdornment, Paper, SvgIconTypeMap, TextField} from "@mui/material";
import CloseRoundedIcon from "@mui/icons-material/CloseRounded";
import DoneRoundedIcon from "@mui/icons-material/DoneRounded";
import {observer} from "mobx-react-lite";
import {OverridableComponent} from "@mui/material/OverridableComponent";
import {useSearchParams} from "react-router-dom";

export interface TextFieldFilterProps {
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

const TextFieldFilter = observer(({
                                    isFieldFocused,
                                    setIsFieldFocused,
                                    setFieldValue,
                                    fieldValue,
                                    InputIcon,
                                    label,
                                    idPrefix,
                                    queryParam,
                                    fetchResults
                                  }: TextFieldFilterProps) => {
  const [searchParams, setSearchParams] = useSearchParams();

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

  return (
    <Grid container direction={"column"} position={"relative"}>
      <Grid xs={12}>
        <Box sx={{alignItems: 'center'}}>
          <TextField
            fullWidth
            value={fieldValue}
            onChange={(event) => {
              setTextField(event.target.value, queryParam)
            }}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start" sx={{color: 'action.active', mr: 2.5, my: 1}}>
                  <Badge color={"primary"} variant={"dot"} invisible={fieldValue == ""}>
                    {InputIcon && <InputIcon sx={{color: 'action.active', mr: -0.5, my: -0.5}} fontSize="large"/>}
                  </Badge>
                </InputAdornment>
              ),
            }}
            id={`${idPrefix}-filter-input`} label={label} variant="outlined"
            onBlur={() => {
              setIsFieldFocused(false)
              fetchResults()
            }}
            onFocus={() => {
              setIsFieldFocused(true)
            }}
            onKeyDown={(event) => {
              if (event.key === "Enter" && document.activeElement instanceof HTMLElement) {
                document.activeElement.blur()
              }
            }
            }
          />
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
            <Paper sx={{p: 2}}>
              <Grid container spacing={2}>
                <Grid xs={12} md={6}>
                  <Button id={`${idPrefix}-filter-reset-button`} aria-haspopup="true"
                          aria-controls={`${idPrefix}-filter-menu-reset`}
                          aria-label={`${label} Filter Reset`} variant="contained" color="info" size="large"
                          endIcon={<CloseRoundedIcon/>} fullWidth onMouseDown={() => setTextField("", queryParam)}>
                    Reset Filter
                  </Button>
                </Grid>
                <Grid xs={12} md={6}>
                  <Button id={`${idPrefix}-filter-apply-button`} aria-haspopup="true"
                          aria-controls={`${idPrefix}-filter-menu-apply`}
                          aria-label={`${label} Filter Apply`} variant="contained" color="primary" size="large"
                          endIcon={<DoneRoundedIcon/>} fullWidth>
                    Apply Filter
                  </Button>
                </Grid>

              </Grid>


            </Paper>
          </Grid>
        )
      }

    </Grid>
  )
})

export default TextFieldFilter;
