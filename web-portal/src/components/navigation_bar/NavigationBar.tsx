// Adapted from https://mui.com/material-ui/react-drawer/
import {useContext} from "react";
import {CSSObject, styled, Theme} from "@mui/material/styles";
import MuiDrawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import IconButton from "@mui/material/IconButton";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import HomeRoundIcon from '@mui/icons-material/Home';
import HistoryRoundIcon from "@mui/icons-material/History";
import LogoutIcon from "@mui/icons-material/Logout";
import {useNavigate} from "react-router-dom";
import {Typography} from "@mui/material";
import ScheduleSendRoundIcon from '@mui/icons-material/ScheduleSend';
import MonitorHeartRoundedIcon from '@mui/icons-material/MonitorHeartRounded';
import SettingsRoundedIcon from '@mui/icons-material/SettingsRounded';
import StorageRoundedIcon from '@mui/icons-material/StorageRounded';
import LightModeRoundedIcon from '@mui/icons-material/LightModeRounded';
import DarkModeRoundedIcon from '@mui/icons-material/DarkModeRounded';
import {SnackbarContext} from "../../context/SnackbarContext";
import logo from "../../assets/logo.png";
import {ConfigContext} from "../../context/ConfigContext.tsx";
import {useStore} from "../../context/StoreContext.tsx";
import {observer} from "mobx-react-lite";

const drawerWidth = 340;


const openedMixin = (theme: Theme): CSSObject => ({
  width: drawerWidth,
  transition: theme.transitions.create("width", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.enteringScreen,
  }),
  overflowX: "hidden",
});

const closedMixin = (theme: Theme): CSSObject => ({
  transition: theme.transitions.create("width", {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  overflowX: "hidden",
  width: `calc(${theme.spacing(7)} + 1px)`,
  [theme.breakpoints.up("sm")]: {
    width: `calc(${theme.spacing(8)} + 1px)`,
  },
});

const DrawerHeader = styled("div")(({theme}) => ({
  display: "flex",
  alignItems: "center",
  justifyContent: "flex-end",
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
}));

const Drawer = styled(MuiDrawer, {
  shouldForwardProp: (prop: string) => prop !== "open",
})(({theme, open}) => ({
  width: drawerWidth,
  flexShrink: 0,
  whiteSpace: "nowrap",
  boxSizing: "border-box",
  ...(open && {
    ...openedMixin(theme),
    "& .MuiDrawer-paper": openedMixin(theme),
  }),
  ...(!open && {
    ...closedMixin(theme),
    "& .MuiDrawer-paper": closedMixin(theme),
  }),
}));

const NavigationBar = observer(() => {
  const navigate = useNavigate();
  const {addSnackbar} = useContext(SnackbarContext);
  const [config] = useContext(ConfigContext);
  const rootStore = useStore()

  const toggleDrawer = () => {
    if (rootStore.platformPreferences.navigationBarOpen) {
      rootStore.setNavigationBarOpen(false);
    } else {
      rootStore.setNavigationBarOpen(true);
    }
  };

  const getColor = (path: string) => {
    return window.location.pathname.replace("/", "") === path ? "primary" : "inherit";
  }

  return <><Drawer variant="permanent" open={rootStore.platformPreferences.navigationBarOpen}>
    <DrawerHeader style={{justifyContent: "flex-start"}}>
      <img id={"app-logo"} src={logo} alt="Platform Logo" style={{maxWidth: "48px", cursor: "pointer"}}
           onClick={() => navigate("/")}/>
    </DrawerHeader>
    <DrawerHeader
      sx={{
        justifyContent: rootStore.platformPreferences.navigationBarOpen ? "flex-end" : "center",
      }}
    >
      <IconButton onClick={toggleDrawer} title={"Expand menu"} id={"navbar-expand"}>
        {!rootStore.platformPreferences.navigationBarOpen ? <ChevronRightIcon/> : <ChevronLeftIcon/>}
      </IconButton>
    </DrawerHeader>
    <Divider/>
    <List>
      <ListItem disablePadding sx={{display: "block"}} key={"home"}>
        <ListItemButton
          id={"nav-home"}
          onClick={() => navigate("/")}
          sx={{
            minHeight: 48,
            justifyContent: rootStore.platformPreferences.navigationBarOpen ? "initial" : "center",
            px: 2.5,
          }}
        >
          <ListItemIcon
            sx={{
              minWidth: 0,
              mr: rootStore.platformPreferences.navigationBarOpen ? 3 : "auto",
              justifyContent: "center",
            }}
          >
            <HomeRoundIcon color={getColor("")}/>
          </ListItemIcon>
          <ListItemText primary={"Home"} sx={{opacity: rootStore.platformPreferences.navigationBarOpen ? 1 : 0}}
                        id={"nav-home-label"}/>
        </ListItemButton>
      </ListItem>

      <ListItem disablePadding sx={{display: "block"}} key={"communication-history"}>
        <ListItemButton
          id={"nav-history"}
          onClick={() => navigate("/history")}
          sx={{
            minHeight: 48,
            justifyContent: rootStore.platformPreferences.navigationBarOpen ? "initial" : "center",
            px: 2.5,
          }}
        >
          <ListItemIcon
            sx={{
              minWidth: 0,
              mr: rootStore.platformPreferences.navigationBarOpen ? 3 : "auto",
              justifyContent: "center",
            }}
          >
            <HistoryRoundIcon color={getColor("history")}/>
          </ListItemIcon>
          <ListItemText primary={"Communication History"}
                        sx={{opacity: rootStore.platformPreferences.navigationBarOpen ? 1 : 0}}
                        id={"nav-history-label"}/>
        </ListItemButton>
      </ListItem>

      <ListItem disablePadding sx={{display: "block"}} key={"communication-schedules"}>
        <ListItemButton
          id={"nav-schedules"}
          onClick={() => navigate("/schedules")}
          sx={{
            minHeight: 48,
            justifyContent: rootStore.platformPreferences.navigationBarOpen ? "initial" : "center",
            px: 2.5,
          }}
        >
          <ListItemIcon
            sx={{
              minWidth: 0,
              mr: rootStore.platformPreferences.navigationBarOpen ? 3 : "auto",
              justifyContent: "center",
            }}
          >
            <ScheduleSendRoundIcon color={getColor("schedules")}/>
          </ListItemIcon>
          <ListItemText primary={"Communication Schedules"}
                        sx={{opacity: rootStore.platformPreferences.navigationBarOpen ? 1 : 0}}
                        id={"nav-schedules-label"}/>
        </ListItemButton>
      </ListItem>

      <ListItem disablePadding sx={{display: "block"}} key={"communication-gateways"}>
        <ListItemButton
          id={"nav-gateways"}
          onClick={() => navigate("/gateways")}
          sx={{
            minHeight: 48,
            justifyContent: rootStore.platformPreferences.navigationBarOpen ? "initial" : "center",
            px: 2.5,
          }}
        >
          <ListItemIcon
            sx={{
              minWidth: 0,
              mr: rootStore.platformPreferences.navigationBarOpen ? 3 : "auto",
              justifyContent: "center",
            }}
          >
            <StorageRoundedIcon color={getColor("gateways")}/>
          </ListItemIcon>
          <ListItemText primary={"Communication Gateways"}
                        sx={{opacity: rootStore.platformPreferences.navigationBarOpen ? 1 : 0}}
                        id={"nav-gateways-label"}/>
        </ListItemButton>
      </ListItem>

      <ListItem disablePadding sx={{display: "block"}} key={"platform-preferences"}>
        <ListItemButton
          id={"nav-preferences"}
          onClick={() => navigate("/preferences")}
          sx={{
            minHeight: 48,
            justifyContent: rootStore.platformPreferences.navigationBarOpen ? "initial" : "center",
            px: 2.5,
          }}
        >
          <ListItemIcon
            sx={{
              minWidth: 0,
              mr: rootStore.platformPreferences.navigationBarOpen ? 3 : "auto",
              justifyContent: "center",
            }}
          >
            <SettingsRoundedIcon color={getColor("preferences")}/>
          </ListItemIcon>
          <ListItemText primary={"Platform Preferences"}
                        sx={{opacity: rootStore.platformPreferences.navigationBarOpen ? 1 : 0}}
                        id={"nav-preferences-label"}/>
        </ListItemButton>
      </ListItem>

      <ListItem disablePadding sx={{display: "block"}} key={"platform-monitoring"}>
        <ListItemButton
          id={"nav-stats"}
          onClick={() => navigate("/stats")}
          sx={{
            minHeight: 48,
            justifyContent: rootStore.platformPreferences.navigationBarOpen ? "initial" : "center",
            px: 2.5,
          }}
        >
          <ListItemIcon
            sx={{
              minWidth: 0,
              mr: rootStore.platformPreferences.navigationBarOpen ? 3 : "auto",
              justifyContent: "center",
            }}
          >
            <MonitorHeartRoundedIcon color={getColor("stats")}/>
          </ListItemIcon>
          <ListItemText primary={"Platform Monitoring"}
                        sx={{opacity: rootStore.platformPreferences.navigationBarOpen ? 1 : 0}}
                        id={"nav-monitoring-label"}/>
        </ListItemButton>
      </ListItem>
    </List>


    <Divider/>


    <List sx={{marginTop: "auto"}}>
      <ListItem disablePadding sx={{display: "block"}}>
        <ListItemButton sx={{
          minHeight: 48,
          justifyContent: rootStore.platformPreferences.navigationBarOpen ? "initial" : "center",
          px: 2.5,
        }}
                        onClick={() => {
                          rootStore.setColorTheme(rootStore.platformPreferences.colorTheme == "dark" ? "light" : "dark");
                        }}
        >
          <ListItemIcon
            sx={{
              minWidth: 0,
              mr: rootStore.platformPreferences.navigationBarOpen ? 3 : "auto",
              justifyContent: "center",
            }}
          >
            {rootStore.platformPreferences.colorTheme == "light" ? <LightModeRoundedIcon/> : <DarkModeRoundedIcon/>}
          </ListItemIcon>
          <ListItemText primary={rootStore.platformPreferences.colorTheme == "dark" ? "Dark Mode" : "Light Mode"}
                        sx={{opacity: rootStore.platformPreferences.navigationBarOpen ? 1 : 0}}
                        id={"nav-theme-label"}/>
        </ListItemButton>
      </ListItem>
      <ListItem disablePadding sx={{display: "block"}} key={"Version"}>
        <Typography
          variant={"body2"}
          sx={{
            textAlign: "center",
          }}
        >
          v{APP_VERSION}
        </Typography>
      </ListItem>

      <ListItem disablePadding sx={{display: "block"}}>
        <ListItemButton
          sx={{
            minHeight: 48,
            justifyContent: rootStore.platformPreferences.navigationBarOpen ? "initial" : "center",
            px: 2.5,
          }}
          onClick={() => {
            window.localStorage.removeItem(`${APP_VERSION}-${config.environment}-communication-scheduler-token`);
            addSnackbar("Removed authentication cookie", "success");
          }}
        >
          <ListItemIcon
            sx={{
              minWidth: 0,
              mr: rootStore.platformPreferences.navigationBarOpen ? 3 : "auto",
              justifyContent: "center",
            }}
          >
            <LogoutIcon/>
          </ListItemIcon>
          <ListItemText primary="Log out" sx={{opacity: rootStore.platformPreferences.navigationBarOpen ? 1 : 0}}
                        id={"nav-logout-label"}/>
        </ListItemButton>
      </ListItem>
    </List>
  </Drawer></>;
})

export default NavigationBar;
