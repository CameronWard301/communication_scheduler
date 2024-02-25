import {FormControl, InputLabel, MenuItem, Select, SelectChangeEvent} from "@mui/material";

interface TimeSelectionProps {
  keyId: string;
  value: string;
  onChange: (value: SelectChangeEvent) => void;

}

const TimeSelection = (props: TimeSelectionProps) => {
  return (

    <FormControl key={props.keyId + "-key"} margin={"normal"} sx={{pl: 3, minWidth: 200}} id={props.keyId}>
      <InputLabel htmlFor={props.keyId + "-input"} id={props.keyId + "-label"} sx={{pl: 4}}>Time Period</InputLabel>
      <Select
        labelId={props.keyId + "-label"}
        id={props.keyId}
        name={props.keyId + "-name"}
        value={props.value}
        label="Time Period"
        onChange={props.onChange}
        inputProps={{id: props.keyId + "-input"}}
      >
        <MenuItem value={"S"}>Seconds</MenuItem>
        <MenuItem value={"M"}>Minutes</MenuItem>
        <MenuItem value={"H"}>Hours</MenuItem>
        <MenuItem value={"D"}>Days</MenuItem>
      </Select>
    </FormControl>
  )
}

export default TimeSelection;
