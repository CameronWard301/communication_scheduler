import React from "react";
import {Tooltip, tooltipClasses, TooltipProps, Typography} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import HelpRoundedIcon from "@mui/icons-material/HelpRounded";
import {styled} from "@mui/material/styles";

// adapted from: https://mui.com/material-ui/react-tooltip/
const HtmlTooltip = styled(({className, ...props}: TooltipProps) => (
  <Tooltip {...props} classes={{popper: className}} children={props.children}/>
))(({theme}) => ({
  [`& .${tooltipClasses.tooltip}`]: {
    backgroundColor: '#f5f5f9',
    color: 'rgba(0, 0, 0, 0.87)',
    maxWidth: 420,
    fontSize: theme.typography.pxToRem(12),
    border: '1px solid #dadde9',
  },
}));

interface CustomTooltipProps {
  ariaLabelTopic: string;
  message: string;
}

const CustomTooltip = (props: CustomTooltipProps) => {
  return (
    <HtmlTooltip placement={"right-start"}

                 title={
                   <React.Fragment>
                     <Typography variant={"body1"}>{props.message}</Typography>
                   </React.Fragment>
                 }>
      <IconButton sx={{mb: 3}} aria-label={`Help for: ${props.ariaLabelTopic}`}>
        <HelpRoundedIcon/>
      </IconButton>
    </HtmlTooltip>
  );
}

export default CustomTooltip;
