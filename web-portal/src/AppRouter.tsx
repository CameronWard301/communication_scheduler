import {Route, Routes} from "react-router-dom";
import {lazy, Suspense} from "react";


const Home = lazy(() => import ("./pages/Home.tsx"))
const AddGateway = lazy(() => import ("./pages/Gateway/AddGateway.tsx"))
const EditGateway = lazy(() => import ("./pages/Gateway/EditGateway.tsx"))
const GatewayTable = lazy(() => import ("./pages/Gateway/GatewayTable.tsx"))
const ScheduleTable = lazy(() => import ("./pages/Schedule/ScheduleTable.tsx"))
const AddSchedule = lazy(() => import ("./pages/Schedule/AddSchedule.tsx"))
const Preferences = lazy(() => import ("./pages/Preferences.tsx"))
const EditSchedule = lazy(() => import ("./pages/Schedule/EditSchedule.tsx"))
const BulkAction = lazy(() => import ("./pages/Schedule/BulkScheduleAction.tsx"))
const HistoryTable = lazy(() => import ("./pages/HistoryTable.tsx"))
const Stats = lazy(() => import ("./pages/Stats.tsx"))

const AppRouter = () => {

  return (
    <Routes>
      <Route path="/" element={<Suspense><Home/></Suspense>}/>
      <Route path="/preferences" element={<Suspense><Preferences/></Suspense>}/>
      <Route path="/gateways" element={<Suspense><GatewayTable/></Suspense>}/>
      <Route path="/gateway/:id" element={<Suspense><EditGateway/></Suspense>}/>
      <Route path="/add-gateway" element={<Suspense><AddGateway/></Suspense>}/>
      <Route path={"/schedules"} element={<Suspense><ScheduleTable/></Suspense>}/>
      <Route path={"/schedule/:id"} element={<Suspense><EditSchedule/></Suspense>}/>
      <Route path={"/add-schedule"} element={<Suspense><AddSchedule/></Suspense>}/>
      <Route path={"/schedule/actions"} element={<Suspense><BulkAction/></Suspense>}/>
      <Route path={"/history"} element={<Suspense><HistoryTable/></Suspense>}/>
      <Route path={"/stats"} element={<Suspense><Stats/></Suspense>}/>
    </Routes>
  )
}
export default AppRouter
