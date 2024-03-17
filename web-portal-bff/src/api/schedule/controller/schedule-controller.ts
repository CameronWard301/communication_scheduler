import express from "express";
import {ScheduleService} from "../service/schedule-service";
import {errorHandler} from "../../../helper/error-handler";

const router = express.Router();

router.get("/v1/bff/schedule", (req, res) => {
  ScheduleService().getSchedules(req.headers.authorization, req.query).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  })
})

router.post("/v1/bff/schedule", (req, res) => {
  ScheduleService().createSchedule(req.headers.authorization, req.body).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  })

})

router.get("/v1/bff/schedule/:id", (req, res) => {
  ScheduleService().getScheduleById(req.headers.authorization, req.params.id).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  })
})

router.patch("/v1/bff/schedule/:id/pause", (req, res) => {
  ScheduleService().pauseSchedule(req.headers.authorization, req.params.id).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  })
})

router.patch("/v1/bff/schedule/:id/resume", (req, res) => {
  ScheduleService().resumeSchedule(req.headers.authorization, req.params.id).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  })
})

module.exports = router;
export default router;
