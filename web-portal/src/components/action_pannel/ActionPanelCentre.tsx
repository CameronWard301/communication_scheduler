import Box from "@mui/material/Box";
import * as React from "react";
import Grid from "@mui/material/Unstable_Grid2";

type Props = {
  children: React.ReactNode;
  minHeight?: string;
};

export const ActionPanelCentre = ({children, minHeight = "70vh"}: Props) => {
  return (
    <Grid xs={12} container spacing={0} direction="column" alignItems="center" justifyContent="center"
          sx={{minHeight: minHeight}}>
      <Box>
        <Grid container spacing={2} justifyContent={"center"}>
          {children}
        </Grid>
      </Box>
    </Grid>
  );
};
