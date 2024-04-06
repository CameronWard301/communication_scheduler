import express from "express";
import { ScheduleService } from "../service/schedule-service";
import { errorHandler } from "../../../helper/error-handler";

const router = express.Router();

router.get("/v1/bff/schedule", (req, res) => {
  // #swagger.tags = ["Schedules"]
  // #swagger.description = "Get schedules."
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
    #swagger.parameters['gatewayId'] = {
            in: 'query',
            description: 'Gateway ID to filter results by',
            type: 'string'
    }
    #swagger.parameters['scheduleId'] = {
            in: 'query',
            description: 'Schedule ID to filter results by',
            type: 'string'
    }
    #swagger.parameters['userId'] = {
            in: 'query',
            description: 'User ID to filter results by',
            type: 'string'
    }

    #swagger.responses[200] = {
          description: 'Get the schedules from the server matching the filters.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/ClientSchedulePage' }
                }
            }
        }

    #swagger.responses[401] = {
            description: 'Unauthorized.'
           }
    }
    #swagger.responses[403] = {
            description: 'Forbidden.'
    }

   */
  ScheduleService().getSchedules(req.headers.authorization, req.query).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.get("/v1/bff/schedule/review", (req, res) => {
  // #swagger.tags = ["Schedules"]
  // #swagger.description = "Get schedules for bulk action."
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
    #swagger.parameters['gatewayId'] = {
            in: 'query',
            description: 'Gateway ID to filter results by',
            type: 'string'
    }
    #swagger.parameters['scheduleId'] = {
            in: 'query',
            description: 'Schedule ID to filter results by',
            type: 'string'
    }
    #swagger.parameters['userId'] = {
            in: 'query',
            description: 'User ID to filter results by',
            type: 'string'
    }
    #swagger.parameters['scheduleIds'] = {
            in: 'query',
            description: 'a JSON-URL-ENCODED list of schedule ids to filter results by',
            type: 'string'
    }
    #swagger.parameters['selectionType'] = {
            in: 'query',
            description: 'The type of selection to filter results by, either \"IDs\" or \"QUERY\"',
            type: 'string'
    }

    #swagger.responses[200] = {
          description: 'Get the schedules from the server matching the filters.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/ClientSchedulePage' }
                }
            }
        }

    #swagger.responses[401] = {
            description: 'Unauthorized.'
           }
    }
    #swagger.responses[403] = {
            description: 'Forbidden.'
    }

   */
  ScheduleService().getSchedulesForBulkAction(req.headers.authorization, req.query).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.post("/v1/bff/schedule", (req, res) => {
  // #swagger.tags = ["Schedules"]
  // #swagger.description = "Create a schedule. Please provide one of interval, calendar, or cronExpression."
  /* #swagger.parameters['body'] = {
              in: 'body',
              description: 'The schedule to create',
              required: true,
              schema: { $ref: '#/components/schemas/ClientScheduleCreateRequest' }
      }

      #swagger.responses[201] = {
            description: 'The created schedule from the server.',
               content: {
                  "application/json": {
                      schema: { $ref: '#/components/schemas/ClientSchedule' }
                  }
              }
          }
      #swagger.responses[400] = {
              description: 'Bad request.'
      }
      #swagger.responses[401] = {
              description: 'Unauthorized.'
      }
      #swagger.responses[403] = {
              description: 'Forbidden.'
      }

     */

  ScheduleService().createSchedule(req.headers.authorization, req.body).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.put("/v1/bff/schedule", (req, res) => {
  ScheduleService().updateSchedule(req.headers.authorization, req.body).then((response) => {
    // #swagger.tags = ["Schedules"]
    // #swagger.description = "Update a schedule."
    /* #swagger.parameters['body'] = {
                    in: 'body',
                    description: 'The schedule to update',
                    required: true,
                    schema: { $ref: '#/components/schemas/ClientScheduleEditRequest' }
            }

            #swagger.responses[200] = {
                  description: 'The updated schedule from the server.',
                     content: {
                        "application/json": {
                            schema: { $ref: '#/components/schemas/ClientScheduleCreateEdit' }
                        }
                    }
                }
            #swagger.responses[400] = {
                        description: 'Bad request.'
            }
            #swagger.responses[401] = {
                    description: 'Unauthorized.'
            }
            #swagger.responses[403] = {
                    description: 'Forbidden.'
            }
            #swagger.responses[404] = {
                    description: 'Not Found.'
            }

           */
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.patch("/v1/bff/schedule", (req, res) => {
  // #swagger.tags = ["Schedules"]
  // #swagger.description = "Bulk update schedules."
  /*
  #swagger.parameters['scheduleIds'] = {
        in: 'query',
        description: 'The JSON URL-ENCODED list of schedule IDs to update',
        type: 'string'
  }
  #swagger.parameters['selectionType'] = {
        in: 'query',
        description: 'The type of selection to filter results by, either \"IDs\" or \"QUERY\". If set to query one of userId, gatewayId or scheduleIds should be set',
        type: 'string'
  }
  #swagger.parameters['userId'] = {
        in: 'query',
        description: 'The user ID to select schedules by if selectionType is set to query',
        type: 'string'
  }
  #swagger.parameters['gatewayId'] = {
        in: 'query',
        description: 'The gateway ID to select schedules by if selectionType is set to query',
        type: 'string'
  }


  #swagger.parameters['body'] = {
            in: 'body',
            description: 'The properties of the schedules to update, action type can be Pause, Resume, Delete or Gateway. If action type is Gateway, gatewayId is required.',
            required: true,
            schema: { $ref: '#/components/schemas/ClientBulkUpdateScheduleRequest' }
    }

    #swagger.responses[200] = {
          description: 'The updated schedules from the server.',
             content: {
                "application/json": {
                    schema: { $ref: '#/components/schemas/BulkActionResult' }
                }
            }
        }
    #swagger.responses[202] = {
        description: 'Some schedules were updated but some failed.',
           content: {
              "application/json": {
                  schema: { $ref: '#/components/schemas/BulkActionResult' }
              }
          }
      }
    #swagger.responses[400] = {
            description: 'Bad request.'
    }
    #swagger.responses[401] = {
            description: 'Unauthorized.'
    }
    #swagger.responses[403] = {
            description: 'Forbidden.'
    }

   */
  ScheduleService().bulkUpdateSchedules(req.headers.authorization, req.body, req.query).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.get("/v1/bff/schedule/:id", (req, res) => {
  ScheduleService().getScheduleById(req.headers.authorization, req.params.id).then((response) => {
    // #swagger.tags = ["Schedules"]
    // #swagger.description = "Get a schedule by ID."
    /* #swagger.responses[200] = {
              description: 'The schedule from the server.',
                 content: {
                    "application/json": {
                        schema: { $ref: '#/components/schemas/ClientSchedule' }
                    }
                }
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
        }
     */
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.delete("/v1/bff/schedule/:id", (req, res) => {
  ScheduleService().deleteScheduleById(req.headers.authorization, req.params.id).then((response) => {
    // #swagger.tags = ["Schedules"]
    // #swagger.description = "Delete a schedule by ID."
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
        }
     */
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.patch("/v1/bff/schedule/:id/pause", (req, res) => {
  ScheduleService().pauseSchedule(req.headers.authorization, req.params.id, req.body).then((response) => {
    // #swagger.tags = ["Schedules"]
    // #swagger.description = "Pause a schedule by ID."
    /*#swagger.parameters['body'] = {
      in: 'body',
      description: 'The gateway name to be returned back in the same object',
      required: true,
      schema: { $ref: '#/components/schemas/GatewayName' }
    }

    #swagger.responses[200] = {
          description: 'Schedule was paused successfully.',
           content: {
              "application/json": {
                  schema: { $ref: '#/components/schemas/ClientScheduleCreateEdit' }
              }
          }
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
    }
     */

    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

router.patch("/v1/bff/schedule/:id/resume", (req, res) => {
  ScheduleService().resumeSchedule(req.headers.authorization, req.params.id, req.body).then((response) => {
    // #swagger.tags = ["Schedules"]
    // #swagger.description = "Resume a schedule by ID."
    /* #swagger.parameters['body'] = {
       in: 'body',
       description: 'The gateway name to be returned back in the same object',
       required: true,
       schema: { $ref: '#/components/schemas/GatewayName' }
      }

    #swagger.responses[200] = {
          description: 'Schedule was resumed successfully.',
           content: {
              "application/json": {
                  schema: { $ref: '#/components/schemas/ClientScheduleCreateEdit' }
              }
          }
    }
    #swagger.responses[401] = {
            description: 'Unauthorized.'
           }
    #swagger.responses[403] = {
            description: 'Forbidden.'
    }
    #swagger.responses[404] = {
            description: 'Not Found.'
    }
     */
    res.status(response.status).send(response.data);
  }).catch((reason) => {
    errorHandler(res, reason);
  });
});

module.exports = router;
export default router;
