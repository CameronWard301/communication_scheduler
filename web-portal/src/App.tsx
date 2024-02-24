import {CssBaseline, ThemeProvider, createTheme} from '@mui/material'
import './App.css'
import {ConfigProvider} from "./context/ConfigContext.tsx";
import {SnackbarContextProvider} from "./context/SnackbarContext.tsx";
import {RootStore} from "./stores/RootStore.tsx";
import {StoreProvider} from "./context/StoreContext.tsx";

const theme = createTheme({
  palette: {
    mode: 'dark'
  }
})

const store = new RootStore();

// noinspection JSUnusedGlobalSymbols
function App() {

  return (
    <>
      <StoreProvider store={store}>
        <ThemeProvider theme={theme}>
          <ConfigProvider>
            <SnackbarContextProvider>
              <CssBaseline/>
              <h1>Vite + React</h1>
              <div className="card">


              </div>
              <p className="read-the-docs">
                Click on the Vite and React logos to learn more
              </p>
            </SnackbarContextProvider>
          </ConfigProvider>
        </ThemeProvider>
      </StoreProvider>
    </>
  )
}

// noinspection JSUnusedGlobalSymbols
export default App
