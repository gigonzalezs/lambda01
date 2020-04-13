package wabilytics.aalShiftsBusy

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

@DatabaseTable(tableName = "all_shifts_busy_log")
data class AllShiftsBusyLog(
        @DatabaseField(generatedId = true, canBeNull = false) var id: UUID? = null,
        @DatabaseField var customerId: String? = null,
        @DatabaseField var latitude: Double? = null,
        @DatabaseField var longitude: Double? = null,
        @DatabaseField var created: Date? = Date()
) {
    constructor() : this(null, null, null, null, null) {
        // ORMLite needs a no-arg constructor
    }
}