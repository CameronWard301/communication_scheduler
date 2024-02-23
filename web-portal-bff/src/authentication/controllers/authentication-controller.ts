import express from "express";
import { AuthToken } from "../model/auth-models";

const router = express.Router();

router.post("/auth", (req, res) => {
  // #swagger.tags = ["Auth"]
  // #swagger.description = "BFF Auth APIs"
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
  return res.status(200).send({ token: "my", expires: "soon" } as AuthToken);
});

module.exports = router;
export default router;
