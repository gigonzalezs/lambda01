package wabilytics

import com.fasterxml.jackson.databind.JsonNode

data class EventRequest (
        var subject: String?,
        var payload: JsonNode?) {

    constructor() : this(null, null) {
        // ORMLite needs a no-arg constructor
    }
}