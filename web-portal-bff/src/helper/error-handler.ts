import { Response } from "express";
import { AxiosError } from "axios";

export const errorHandler = (response: Response, error: AxiosError) => {
  if (error.response) {
    response.status(error.response.status).send(error.response.data);
    console.debug("Error: ", error);
  } else if (error.request) {
    console.log("Error: ", error);
    response.status(500).send("No response from server");
  } else {
    console.log("Error: ", error);
    response.status(500).send("Internal Server Error");
  }
};
