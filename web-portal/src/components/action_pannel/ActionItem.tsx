import {Card, Fade, Typography, useTheme} from "@mui/material";
import React from "react";
import Box from "@mui/material/Box";
import Grid from '@mui/material/Unstable_Grid2/Grid2';
import {Variant} from "@mui/material/styles/createTypography";

type Props = {
  title: string;
  subtitle?: string;
  onClick: () => void;
  Icon?: React.ElementType;
  disabled?: boolean;
  size: "small" | "large";
  selected?: boolean;
  key: string;
  componentName: string;
};

const sizes = {
  small: {
    width: 150,
    titleVariant: "h6",
    iconSize: "2rem",
  },
  large: {
    width: 300,
    titleVariant: "h4",
    iconSize: "7rem",
  },
};

export const ActionItem = ({title, subtitle, onClick, Icon, size, selected = false, disabled = false, componentName}: Props) => {
  const theme = useTheme();

  return (
    <Fade in={true}>
      <Box display={"inline-block"} width={sizes[size].width} marginTop={2} marginX={1} justifyContent={"center"}>
        {disabled && (
          <Card
            elevation={3}
            id={componentName+"-card"}
            sx={{
              cursor: "pointer",
              height: "100%",
              ":hover": {boxShadow: 10},
              border: 1,
              borderColor: theme.palette.primary.main
            }}
          >
            <Grid sx={{display: "flex", flexDirection: "column", height: "100%"}}>
              <Typography variant={sizes[size].titleVariant as Variant} sx={{marginTop: 2, marginX: 0.5}}
                          textAlign={"center"} id={componentName+"-title"}>
                {title}
              </Typography>
              <Typography variant={"subtitle1"} textAlign={"center"} id={componentName+"-subtitle"}>
                {subtitle}
              </Typography>
              <Box
                sx={{
                  my: 3,
                  flexGrow: 1,
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "flex-end",
                  alignItems: "center",
                }}
              >
                {Icon && <Icon sx={{fontSize: sizes[size].iconSize}}/>}
              </Box>
            </Grid>
          </Card>
        )}
        {!selected && !disabled && (
          <Card
            id={componentName+"-card"}
            elevation={3}
            onClick={() => {
              onClick();
            }}
            sx={{
              cursor: "pointer",
              height: "100%",
              ":hover": {boxShadow: 10},
              border: 1,
              borderColor: theme.palette.primary.main,
              p: 2
            }}
          >
            <Grid sx={{display: "flex", flexDirection: "column", height: "100%"}}>
              <Box
                sx={{
                  flexGrow: 1,
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "flex-start",
                  alignItems: "center",
                }}
              >
                {Icon && <Icon sx={{fontSize: sizes[size].iconSize}}/>}
              </Box>
              <Typography variant={sizes[size].titleVariant as Variant} sx={{marginY: 2, marginX: 0.5}}
                          textAlign={"center"} id={componentName+"-title"}>
                {title}
              </Typography>

              <Typography variant={"subtitle1"} textAlign={"center"} id={componentName+"-subtitle"}>
                {subtitle}
              </Typography>

            </Grid>
          </Card>
        )}
        {selected && (
          <Card
            id={componentName+"-card"}
            elevation={3}
            onClick={() => {
              onClick();
            }}
            sx={{
              cursor: "pointer",
              height: "100%",
              ":hover": {boxShadow: 10},
              border: 1,
              borderColor: theme.palette.primary.main
            }}
          >
            <Grid sx={{display: "flex", flexDirection: "column", height: "100%"}}>
              <Typography variant={sizes[size].titleVariant as Variant} sx={{marginTop: 2, marginX: 0.5}}
                          color={"white"} textAlign={"center"} id={componentName+"-title"}>
                {title}
              </Typography>
              <Typography variant={"subtitle1"} textAlign={"center"} color={"white"} id={componentName+"-subtitle"}>
                {subtitle}
              </Typography>
              <Box
                sx={{
                  my: 3,
                  flexGrow: 1,
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "flex-end",
                  alignItems: "center",
                }}
              >
                {Icon && <Icon sx={{color: "white", fontSize: sizes[size].iconSize}}/>}
              </Box>
            </Grid>
          </Card>
        )}
      </Box>
    </Fade>
  );
};
