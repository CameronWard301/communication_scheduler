import express from "express";
import axiosClient from "../../../axios-client";
import { AxiosError } from "axios";
import proxy from "../../../proxy";

const router = express.Router();


router.all("/grafana/api/live/ws", (req, res) => {
  let url = (process.env.GRAFANA_API_URL as string) + req.url;
  console.debug(`Forwarding WebSocket request to stats service: ${url}`);
  proxy.ws(req, res, { target: url });
});

router.all("/grafana/*", async (req, res) => {
  let url = (process.env.GRAFANA_API_URL as string) + req.url;
  console.debug(`Forwarding request to stats service: ${url}`);

  try {
    const { data } = await axiosClient({
      method: req.method,
      url: url,
      data: req.body,
      responseType: 'stream',
      headers: {
        ...req.headers
      }
    });
    data.pipe(res);
  } catch (error) {
    if (error instanceof AxiosError) {
      if (error.response) {
        console.debug(`Proxying error response from stats service: ${error.message}`)
        res.status(error.response.status)
        if (error.response.data.pipe){
          error.response.data.pipe(res)
          return;
        }
      }
    }
    console.error(`Error while proxying request to stats service: ${error}`)
    res.status(500).send("Internal BFF Server error");
  }
});


module.exports = router;
export default router;
