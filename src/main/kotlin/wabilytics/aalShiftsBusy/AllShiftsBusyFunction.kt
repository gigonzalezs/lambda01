package wabilytics.aalShiftsBusy

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import wabilytics.EventRequest

/**
 * Handler for requests to Lambda function.
 */
class AllShiftsBusyFunction : RequestHandler<Any?, String> {

    override fun handleRequest(input: Any?, context: Context): String {
        AllShiftsBusyLogDao.initialize(context)
        val logger = context.logger
        logger.log("AllShiftsBusyFunction invoked - version 1.13\r\n")
        logger.log(String.format("input type: %s\r\n",input?.javaClass?.name))
        logger.log(String.format("input: %s\r\n",input?.toString()))
        val eventRequest = EventRequest.fromMap(input as Map<String, Object>?)
        return try {
            logger.log(String.format("payload type: %s\r\n",eventRequest.payload?.javaClass?.name))
            val log = eventRequest.payload!!.toAllShiftsBusyLog()
            AllShiftsBusyLogDao.save(log)
            "OK"
        } catch (e: Exception) {
            logger.log(String.format("Error of Type %s: %s", e.javaClass.name, e.message))
            //rethrow in order to dispatch message to dead letter queue
            throw e
        }
    }
}
