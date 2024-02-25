import {Route, Routes} from "react-router-dom";
import Home from "./pages/Home.tsx";

const AppRouter = () => {
  return (
    <Routes>
      <Route path="/" element={<Home/>}/>
    </Routes>
  )
}
export default AppRouter
