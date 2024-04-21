import express from "express";
import { AuthToken } from "../model/auth-models";

import { AuthService } from "../service/auth-service";
import { errorHandler } from "../../../helper/error-handler";

const router = express.Router();
router.post("/v1/bff/auth", (req, res) => {
  // #swagger.tags = ["Auth"]
  // #swagger.description = "Get the auth token with the required scopes."
  /*  #swagger.requestBody = {
              required: true,
              schema: { $ref: "#/components/schemas/AuthScopes" }
      } */
  /* #swagger.responses[200] = {
            description: 'Generate an auth token with the required scopes',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/AuthToken' }
                }
            }
    } */
  console.log("getting auth token");
  AuthService().getAuthToken(req.body).then((value) => {
    return res.status(value.status).send(value.data as AuthToken);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

module.exports = router;
export default router;
