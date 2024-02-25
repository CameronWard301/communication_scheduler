import {Route, Routes} from "react-router-dom";
import Home from "./pages/Home.tsx";
import Preferences from "./pages/Preferences.tsx";

const AppRouter = () => {
  return (
    <Routes>
      <Route path="/" element={<Home/>}/>
      <Route path="/preferences" element={<Preferences/>}/>
    </Routes>
  )
}
export default AppRouter
