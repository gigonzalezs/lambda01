package wabilytics

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


data class EventRequest (
        val subject: String?,
        val payload: JsonNode?) {

    companion object {
        val mapper = jacksonObjectMapper()
        fun fromJson(rawRequest: Any? ): EventRequest {
            val event = mapper.readTree(rawRequest?.toString())
            val records = event.get("Records")
            val firstRecord = records[0];
            val sns = firstRecord.get("Sns")
            val subject = sns.get("Subject").toString()
            val payload = sns.get("Message")
            return EventRequest(subject, payload)
        }
        fun fromMap(rawRequest: Map<String, Object>? ): EventRequest {
            val event: JsonNode = mapper.convertValue(rawRequest, object : TypeReference<JsonNode>() {})
            return fromJson(event)
        }
    }
}