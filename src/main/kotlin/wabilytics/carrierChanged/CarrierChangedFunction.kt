package wabilytics.carrierChanged

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import wabilytics.EventRequest
import java.util.*

/**
 * Handler for requests to Lambda function.
 */
class CarrierChangedFunction : RequestHandler<Any?, String> {

    override fun handleRequest(input: Any?, context: Context): String {
        CarrierDao.initialize(context)
        val logger = context.logger
        logger.log("CarrierChangedFunction invoked - version 1.0\r\n")
        logger.log(String.format("input type: %s\r\n",input?.javaClass?.name))
        logger.log(String.format("input: %s\r\n",input?.toString()))
        val eventRequest = EventRequest.fromMap(input as Map<String, Object>?)
        return try {
            val carrier = Carrier(UUID.randomUUID().toString(), eventRequest.payload.toString(), true, false)
            CarrierDao.save(carrier)
            "OK"
        } catch (e: Exception) {
            logger.log(String.format("Error of Type %s: %s", e.javaClass.name, e.message))
            //rethrow in order to dispatch message to dead letter queue
            throw e
        }
    }
}