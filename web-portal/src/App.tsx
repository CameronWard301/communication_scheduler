import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import {createTheme, ThemeProvider} from '@mui/material'
import './App.css'
import {ConfigProvider} from "./context/ConfigContext.tsx";
import {SnackbarContextProvider} from "./context/SnackbarContext.tsx";
import {RootStore} from "./stores/RootStore.tsx";
import {StoreProvider} from "./context/StoreContext.tsx";
import {BrowserRouter} from 'react-router-dom';
import NavigationBar from "./components/navigation_bar";
import AppRouter from "./AppRouter.tsx";
import useAxiosClient from "./client/AxiosClient.ts";
import {AxiosContextProvider} from "./context/AxiosContext.tsx";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import {LocalizationProvider} from "@mui/x-date-pickers";

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#2E8BC0'
    },
    secondary: {
      main: '#2e42c0'
    },
    info: {
      main: '#6e7180'
    },

  }
})

const store = new RootStore();

function App() {
  const client = useAxiosClient();

  return (
    <>
      <StoreProvider store={store}>
        <ThemeProvider theme={theme}>
          <ConfigProvider>
            <SnackbarContextProvider>
              <AxiosContextProvider client={client}>
                <BrowserRouter>
                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <Box sx={{display: 'flex'}}>
                      <CssBaseline/>
                      <NavigationBar/>
                      <AppRouter/>
                    </Box>
                  </LocalizationProvider>
                </BrowserRouter>
              </AxiosContextProvider>
            </SnackbarContextProvider>
          </ConfigProvider>
        </ThemeProvider>
      </StoreProvider>
    </>
  )
}

export default App
