import express from "express";
import HistoryService from "../service/history-service";
import { errorHandler } from "../../../helper/error-handler";

const router = express.Router();

router.get("/v1/bff/history", (req, res) => {
  // #swagger.tags = ['History']
  // #swagger.description = 'Get the history page.'

  /*
  #swagger.parameters['pageSize'] = {
      in: 'query',
      description: 'The number of items to return.',
      required: true,
      type: 'integer'
  }
  #swagger.parameters['pageNumber'] = {
      in: 'query',
      description: 'The page number to return.',
      required: true,
      type: 'integer'
  }
  #swagger.parameters['status'] = {
      in: 'query',
      description: 'The status to filter by, either \"Any Status\", \"Completed\", \"Running\", \"Failed\", \"Terminated\", \"Cancelled\", or \"Unknown\".',
      required: false,
      type: 'string'
  }
  #swagger.parameters['gatewayId'] = {
      in: 'query',
      description: 'The gateway ID to filter by.',
      required: false,
      type: 'string'
  }
  #swagger.parameters['scheduleId'] = {
      in: 'query',
      description: 'The schedule ID to filter by.',
      required: false,
      type: 'string'
  }
  #swagger.parameters['userId'] = {
      in: 'query',
      description: 'The user ID to filter by.',
      required: false,
      type: 'string'
  }

  #swagger.responses[200] = {
    description: 'The history page.',
    content: {
      "application/json": {
        schema: {
          $ref: '#/components/schemas/ClientHistoryPage'
        }
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

  HistoryService().getHistoryPage(req.headers.authorization, req.query).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((error) => {
    errorHandler(res, error);
  });
});

router.delete("/v1/bff/history/stop/:workflowId/:runId", (req, res) => {
  // #swagger.tags = ['History']
  // #swagger.description = 'Stop the communication.'
  /*
  #swagger.parameters['workflowId'] = {
      in: 'path',
      description: 'The workflow ID.',
      required: true,
      type: 'string'

  }
  #swagger.parameters['runId'] = {
      in: 'path',
      description: 'The run ID.',
      required: true,
      type: 'string'
  }
  #swagger.responses[204] = {
    description: 'No Content'
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
    description: 'Not found.'
  }
   */
  HistoryService().stopCommunication(req.headers.authorization, req.params.workflowId, req.params.runId).then((response) => {
    res.status(response.status).send(response.data);
  }).catch((error) => {
    errorHandler(res, error);
  });
});

module.exports = router;
export default router;
