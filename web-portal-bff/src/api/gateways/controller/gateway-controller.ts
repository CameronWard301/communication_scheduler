import { errorHandler } from "../../../helper/error-handler";
import GatewayService from "../service/gateway-service";
import express from "express";
import { BaseGateway, ClientGatewayPage, Gateway, GatewayPageQuery, GatewayUpdateRequest } from "../model/Gateways";
import { ScheduleService } from "../../schedule/service/schedule-service";

const router = express.Router();
router.get("/v1/bff/gateway", (req, res) => {
  // #swagger.tags = ["Gateways"]
  // #swagger.description = "Get gateways."
  /* #swagger.parameters['pageNumber'] = {
            in: 'query',
            description: 'Page Number to get',
            type: 'string'
    }
    #swagger.parameters['pageSize'] = {
            in: 'query',
            description: 'Page Size to get',
            type: 'string'
    }
    #swagger.parameters['friendlyName'] = {
            in: 'query',
            description: 'Friendly name to filter results by',
            type: 'string'
    }
    #swagger.parameters['endpointUrl'] = {
            in: 'query',
            description: 'Endpoint url to filter results by',
            type: 'string'
    }
    #swagger.parameters['description'] = {
            in: 'query',
            description: 'Description to filter results by',
            type: 'string'
    }
    #swagger.parameters['sort'] = {
            in: 'query',
            description: 'The field to sort by',
            type: 'string'
    }
    #swagger.parameters['sortDirection'] = {
            in: 'query',
            description: 'Either asc or desc',
            type: 'string'
    }
   */

  /* #swagger.responses[200] = {
            description: 'Get the gateways from the server matching the filters.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/GatewayPage' }
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
  GatewayService().getGateways(req.headers.authorization, req.query as unknown as GatewayPageQuery).then((value) => {
    res.status(value.status).send(value.data as ClientGatewayPage);
  }).catch((reason) => {
    errorHandler(res, reason);
  });

});

router.get("/v1/bff/gateway/:id", (req, res) => {
  // #swagger.tags = ["Gateways"]
  // #swagger.description = "Get gateways."

  /* #swagger.responses[200] = {
            description: 'The found gateway from the server.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/Gateway' }
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
  GatewayService().getGatewayById(req.headers.authorization, req.params.id).then((value) => {
    res.status(value.status).send(value.data as Gateway);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.get("/v1/bff/gateway/:id/schedule/count", (req, res) => {
  // #swagger.tags = ["Gateways"]
  // #swagger.description = "Get the number of schedules that use this gateway."

  /* #swagger.responses[200] = {
            description: 'The total matching the filter.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/TotalMatches' }
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
  ScheduleService().getScheduleCount(req.headers.authorization, {
    gatewayId: req.params.id
  }).then((value) => {
    res.status(value.status).send(value.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.post("/v1/bff/gateway", (req, res) => {
  // #swagger.tags = ["Gateways"]
  // #swagger.description = "Create gateway."

  /* #swagger.responses[200] = {
            description: 'The created gateway from the server.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/Gateway' }
                }
            }
        }
    #swagger.responses[400] = {
            description: 'Bad Request.'
    }
    #swagger.responses[401] = {
            description: 'Unauthorized.'

    }
    #swagger.responses[403] = {
            description: 'Forbidden.'
    } */
  GatewayService().createGateway(req.headers.authorization, req.body as unknown as BaseGateway).then((value) => {
    res.status(value.status).send(value.data as Gateway);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});


router.put("/v1/bff/gateway", (req, res) => {
  // #swagger.tags = ["Gateways"]
  // #swagger.description = "Update gateway."

  /* #swagger.responses[200] = {
            description: 'The updated gateway from the server.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/Gateway' }
                }
            }
        }
    #swagger.responses[400] = {
            description: 'Bad Request.'
    }
    #swagger.responses[401] = {
            description: 'Unauthorized.'
    }
    #swagger.responses[403] = {
            description: 'Forbidden.'
    }
    #swagger.responses[404] = {
            description: 'Not Found.'
    } */
  GatewayService().updateGateway(req.headers.authorization, req.body as unknown as GatewayUpdateRequest).then((value) => {
    res.status(value.status).send(value.data as Gateway);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.delete("/v1/bff/gateway/:id", (req, res) => {
  // #swagger.tags = ["Gateways"]
  // #swagger.description = "Delete gateway by id."

  /* #swagger.responses[204] = {
            description: 'Delete was successful.'

    }
    #swagger.responses[401] = {
            description: 'Unauthorized.'
           }
    }
    #swagger.responses[403] = {
            description: 'Forbidden.'
    }
    #swagger.responses[404] = {
            description: 'Not Found.'
    } */
  GatewayService().deleteGatewayById(req.headers.authorization, req.params.id).then((value) => {
    res.status(value.status).send();
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});


module.exports = router;
export default router;
