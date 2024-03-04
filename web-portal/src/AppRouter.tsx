import {Route, Routes} from "react-router-dom";
import Home from "./pages/Home.tsx";
import Preferences from "./pages/Preferences.tsx";
import {EditGateway, GatewayTable} from "./pages/Gateway";

const AppRouter = () => {
  return (
    <Routes>
      <Route path="/" element={<Home/>}/>
      <Route path="/preferences" element={<Preferences/>}/>
      <Route path="/gateways" element={<GatewayTable/>}/>
      <Route path="/gateway/:id" element={<EditGateway/>}/>
    </Routes>
  )
}
export default AppRouter
