package wabilytics

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.omg.CORBA.Object

data class EventRequest (
        val subject: String?,
        val payload: Any?) {

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
            val event = mapper.convertValue(rawRequest, object : TypeReference<JsonNode>() {})
            return fromJson(event)
        }
    }
}