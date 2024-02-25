import {FormControl, InputLabel, MenuItem, Select, SelectChangeEvent} from "@mui/material";

interface TimeSelectionProps {
  key: string;
  value: string;
  onChange: (value: SelectChangeEvent) => void;

}

const TimeSelection = (props: TimeSelectionProps) => {
  return (

      <FormControl key={props.key+"-key"} margin={"normal"} sx={{pl: 3, minWidth: 200}}>
        <InputLabel id={props.key+"-label"} sx={{pl: 4}}>Time Period</InputLabel>
        <Select
          labelId={props.key+"-label"}
          id={props.key}
          value={props.value}
          label="Time Period"
          onChange={props.onChange}
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
