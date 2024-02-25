import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import {ThemeProvider, createTheme} from '@mui/material'
import './App.css'
import {ConfigProvider} from "./context/ConfigContext.tsx";
import {SnackbarContextProvider} from "./context/SnackbarContext.tsx";
import {RootStore} from "./stores/RootStore.tsx";
import {StoreProvider} from "./context/StoreContext.tsx";
import {BrowserRouter} from 'react-router-dom';
import NavigationBar from "./components/navigation_bar";
import AppRouter from "./AppRouter.tsx";

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#2E8BC0'
    },
  }
})

const store = new RootStore();

function App() {

  return (
    <>
      <StoreProvider store={store}>
        <ThemeProvider theme={theme}>
          <ConfigProvider>
            <SnackbarContextProvider>
              <BrowserRouter>
                <Box sx={{display: 'flex'}}>
              <CssBaseline/>
              <NavigationBar/>
                <AppRouter/>
                </Box>
              </BrowserRouter>
            </SnackbarContextProvider>
          </ConfigProvider>
        </ThemeProvider>
      </StoreProvider>
    </>
  )
}

export default App
