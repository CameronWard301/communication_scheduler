import express from "express";
import { PreferencesService } from "../service/preferences-service";
import { ClientPreferences } from "../model/Preferences";
import { errorHandler } from "../../../helper/error-handler";

const router = express.Router();

router.get("/v1/bff/preferences", (req, res) => {
  // #swagger.tags = ["Preferences"]
  // #swagger.description = "Get preferences."

  /* #swagger.responses[200] = {
            description: 'Generate the preferences from the server.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/Preferences' }
                }
            }
        }
    #swagger.responses[401] = {
            description: 'Unauthorized.'
           }
    }
    #swagger.responses[403] = {
            description: 'Forbidden.'
    } */
  PreferencesService().getPreferences(req.headers.authorization).then((value) => {
    res.status(value.status).send(value.data as ClientPreferences);
  }).catch((reason) => {
    errorHandler(res, reason);
  });

});

router.put("/v1/bff/preferences", (req, res) => {
  // #swagger.tags = ["Preferences"]
  // #swagger.description = "Update preferences."

  /* #swagger.responses[200] = {
            description: 'Update the preferences on the server.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/Preferences' }
                }
            }
        }
    #swagger.requestBody = {
              required: true,
              schema: { $ref: "#/components/schemas/Preferences" }
      }
    #swagger.responses[401] = {
            description: 'Unauthorized.'
           }
    }
    #swagger.responses[403] = {
            description: 'Forbidden.'
    } */
  PreferencesService().putPreferences(req.headers.authorization, req.body).then((value) => {
    res.status(value.status).send(value.data as ClientPreferences);
  }).catch((reason) => {
    errorHandler(res, reason);
  });

});

module.exports = router;
export default router;
