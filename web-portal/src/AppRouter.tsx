import {Route, Routes} from "react-router-dom";
import Home from "./pages/Home.tsx";
import Preferences from "./pages/Preferences.tsx";
import Gateways from "./pages/Gateways.tsx";

const AppRouter = () => {
  return (
    <Routes>
      <Route path="/" element={<Home/>}/>
      <Route path="/preferences" element={<Preferences/>}/>
      <Route path="/gateways" element={<Gateways/>}/>
    </Routes>
  )
}
export default AppRouter
