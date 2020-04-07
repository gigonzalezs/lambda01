package wabilytics.carrierChanged

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import wabilytics.EventRequest
import wabilytics.GatewayResponse
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.stream.Collectors

/**
 * Handler for requests to Lambda function.
 */
class CarrierChangedFunction : RequestHandler<EventRequest?, Any> {
    override fun handleRequest(input: EventRequest?, context: Context): Any {
        CarrierDao.initialize(context)
        val logger = context.logger
        logger.log("CarrierChangedFunction invoked\r\n")
        logger.log(String.format("input: %s\r\n",input?.toString()))
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"
        headers["X-Custom-Header"] = "application/json"
        return try {
            val pageContents = getPageContents("https://checkip.amazonaws.com")
            val output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents)
            val carrier = Carrier(UUID.randomUUID().toString(), output, true, false)
            CarrierDao.save(carrier)
            GatewayResponse(output, headers, 200)
        } catch (e: Exception) {
            logger.log(String.format("Error of Type %s: %s", e.javaClass.name, e.message))
            GatewayResponse("{}", headers, 500)
        }
    }

    @Throws(IOException::class)
    private fun getPageContents(address: String): String {
        val url = URL(address)
        BufferedReader(InputStreamReader(url.openStream())).use { br -> return br.lines().collect(Collectors.joining(System.lineSeparator())) }
    }
}