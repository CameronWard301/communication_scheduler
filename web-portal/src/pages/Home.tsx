import {Typography} from "@mui/material";
import Grid from '@mui/material/Unstable_Grid2/Grid2';
import {ActionItem, ActionPanelCentre} from "../components/action_pannel";
import {useNavigate} from "react-router-dom";
import HistoryRoundIcon from "@mui/icons-material/History";
import SettingsRoundedIcon from "@mui/icons-material/SettingsRounded";
import MonitorHeartRoundedIcon from "@mui/icons-material/MonitorHeartRounded";
import StorageRoundedIcon from "@mui/icons-material/StorageRounded";
import ScheduleSendRoundIcon from "@mui/icons-material/ScheduleSend";

const Home = () => {
  const navigate = useNavigate();
  return (
    <>

      <Grid container spacing={2} justifyContent={"center"} alignItems={"center"} alignContent={"flex-start"}>
        <Grid xs={12}>
          <Typography variant="h1" fontSize={"4rem"} id={"home-title"}>Communication Scheduling Platform</Typography>
        </Grid>
        <Grid xs={12}>
          <Typography variant="h5" pl={0.5} id={"home-subtitle"}>Choose an item from the menu to get stared</Typography>
        </Grid>
        <ActionPanelCentre>
          <ActionItem title={"Communication History"}
                      onClick={() => navigate("/history")}
                      size={"large"}
                      Icon={HistoryRoundIcon}
                      key={"history"}
                      componentName={"history"}
                      subtitle={"View the status of previous communications sent through the platform"}>

          </ActionItem>
          <ActionItem title={"Communication Schedules"}
                      onClick={() => navigate("/schedules")}
                      size={"large"}
                      Icon={ScheduleSendRoundIcon}
                      key={"schedules"}
                      componentName={"schedules"}
                      subtitle={"Find and manage user's communication schedules or create new schedules"}>

          </ActionItem>
          <ActionItem title={"Communication Gateways"}
                      onClick={() => navigate("/gateways")}
                      size={"large"}
                      Icon={StorageRoundedIcon}
                      key={"gateways"}
                      componentName={"gateways"}
                      subtitle={"View and manage the communication gateways available to the platform"}>

          </ActionItem>
          <ActionItem title={"Platform Configuration"}
                      onClick={() => navigate("/preferences")}
                      size={"large"}
                      Icon={SettingsRoundedIcon}
                      key={"preferences"}
                      componentName={"preferences"}
                      subtitle={"View and manage the platform configuration options (Advanced Users)"}>

          </ActionItem>
          <ActionItem title={"Platform monitoring"}
                      onClick={() => navigate("/stats")}
                      size={"large"}
                      Icon={MonitorHeartRoundedIcon}
                      key={"stats"}
                      componentName={"stats"}
                      subtitle={"View and monitor the performance of the platform and cluster"}>

          </ActionItem>
        </ActionPanelCentre>
      </Grid>
    </>
  )
}

export default Home
