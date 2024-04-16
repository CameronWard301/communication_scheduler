import Grid from "@mui/material/Unstable_Grid2/Grid2";
import {Button, FormControl, InputLabel, MenuItem, Select, Typography} from "@mui/material";
import {RefreshRounded} from "@mui/icons-material";
import {useContext, useState} from "react";
import {ConfigContext} from "../context/ConfigContext.tsx";

const Stats = () => {
  const [config] = useContext(ConfigContext);

  const iFrameStyles = {
    flexGrow: 1,
    border: "none",
    maxWidth: "100%",
  }

  const [refresh, setRefresh] = useState("10");
  const refreshOptions = ["1", "5", "10", "30", "60"];

  const handleRefreshClick = () => {
    document.getElementsByName("grafana-iframe").forEach((element: HTMLElement) => {
      const iframe = element as HTMLIFrameElement;
      iframe.src += '';
    })
  }

  return (
    <>
      <Grid container spacing={4} justifyContent={"left"} alignItems={"center"} alignContent={"flex-start"} display={"flex"}
            width={"100%"}>
        <Grid xs={12} mb={2}>
          <Typography variant="h1" fontSize={"4rem"} id={"stats-page-heading"}>System Monitoring</Typography>
        </Grid>

        <Grid xs={12}>
          <Button variant={"contained"} sx={{height: "56px", mr: 2}} id={"refresh-graphs"} endIcon={<RefreshRounded/>} onClick={() => handleRefreshClick()}>Refresh</Button>
          <FormControl>
            <InputLabel id="select-time-label">Auto Refresh Interval</InputLabel>
            <Select
              sx={{minWidth: "200px"}}
              labelId="select-time-label"
              id="select-refresh-time"
              value={refresh}
              label="Select Refresh Period"
              onChange={(event) => {
                setRefresh(event.target.value);
              }}
            >
              {refreshOptions.map((option) => (
                <MenuItem key={option} value={option}>
                  {option} Seconds
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Grid>



        <Grid xs={12} lg={4} container spacing={1}>
          <Grid xs={12} display={"flex"}>
            <iframe style={iFrameStyles} name={"grafana-iframe"} title={"Cluster memory usage gauge"}
                    src={config.bffGrafanaUrl+ "/d-solo/JABGX_-mz/cluster-monitoring-for-kubernetes?orgId=1&refresh="+ refresh +"s&panelId=4"}></iframe>
          </Grid>

          <Grid xs={6} display={"flex"}>
            <iframe style={iFrameStyles} name={"grafana-iframe"} title={"Cluster memory usage used"}
                    src={config.bffGrafanaUrl+ "/d-solo/JABGX_-mz/cluster-monitoring-for-kubernetes?orgId=1&refresh="+ refresh +"s&panelId=9"
                    }            ></iframe>
          </Grid>
          <Grid xs={6} display={"flex"}>
            <iframe style={iFrameStyles} name={"grafana-iframe"} title={"Cluster total memory available"}
                    src={config.bffGrafanaUrl+ "/d-solo/JABGX_-mz/cluster-monitoring-for-kubernetes?orgId=1&refresh="+ refresh +"s&panelId=10"}
            ></iframe>
          </Grid>
        </Grid>

        <Grid xs={12} lg={4} container spacing={1}>
          <Grid xs={12} display={"flex"}>
            <iframe style={iFrameStyles} name={"grafana-iframe"} title={"Cluster CPU usage gauge"}
                    src={config.bffGrafanaUrl + "/d-solo/JABGX_-mz/cluster-monitoring-for-kubernetes?orgId=1&refresh="+ refresh +"s&panelId=6"}></iframe>
          </Grid>

          <Grid xs={6} display={"flex"}>
            <iframe style={iFrameStyles} name={"grafana-iframe"} title={"Cluster CPU usage used"}
                    src={config.bffGrafanaUrl + "/d-solo/JABGX_-mz/cluster-monitoring-for-kubernetes?orgId=1&refresh="+ refresh +"s&panelId=11"}
            ></iframe>
          </Grid>
          <Grid xs={6} display={"flex"}>
            <iframe style={iFrameStyles} name={"grafana-iframe"} title={"Cluster total CPU available"}
                    src={config.bffGrafanaUrl + "/d-solo/JABGX_-mz/cluster-monitoring-for-kubernetes?orgId=1&refresh="+ refresh +"s&panelId=12"}
            ></iframe>
          </Grid>
        </Grid>

        <Grid xs={12} lg={4} container spacing={1}>
          <Grid xs={12} display={"flex"}>
            <iframe style={iFrameStyles} name={"grafana-iframe"} title={"Cluster disk usage gauge"}
                    src={config.bffGrafanaUrl + "/d-solo/JABGX_-mz/cluster-monitoring-for-kubernetes?orgId=1&refresh="+ refresh +"s&panelId=7"}></iframe>
          </Grid>

          <Grid xs={6} display={"flex"}>
            <iframe style={iFrameStyles} name={"grafana-iframe"} title={"Cluster disk usage used"}
                    src={config.bffGrafanaUrl + "/d-solo/JABGX_-mz/cluster-monitoring-for-kubernetes?orgId=1&refresh="+ refresh +"s&panelId=13"}
            ></iframe>
          </Grid>
          <Grid xs={6} display={"flex"}>
            <iframe style={iFrameStyles} name={"grafana-iframe"} title={"Cluster total disk available"}
                    src={config.bffGrafanaUrl + "/d-solo/JABGX_-mz/cluster-monitoring-for-kubernetes?orgId=1&refresh="+ refresh +"s&panelId=14"}
            ></iframe>
          </Grid>
        </Grid>

        <Grid xs={12} display={"flex"} sx={{minHeight: "300px"}}>
          <iframe src={config.bffGrafanaUrl + "/d-solo/edif6eeykmrcwd/custom-dashboard?orgId=1&refresh="+refresh+"&panelId=2"} name={"grafana-iframe"}
                  style={iFrameStyles} title={"Workflow completion graph"}></iframe>
        </Grid>
        <Grid xs={4} display={"flex"} sx={{minHeight: "300px"}}>
          <iframe title={"Total communications sent in the last 24 hours"}
            src={config.bffGrafanaUrl + "/d-solo/edif6eeykmrcwd/custom-dashboard?orgId=1&refresh=" + refresh + "&panelId=1"}
            name={"grafana-iframe"}
            style={iFrameStyles}></iframe>
        </Grid>


      </Grid>
    </>

  );
}

export default Stats;
