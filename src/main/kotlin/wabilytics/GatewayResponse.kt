package wabilytics

/**
 * POJO containing response object for API Gateway.
 */
data class GatewayResponse(val body: String, val headers: Map<String, String>?, val statusCode: Int)