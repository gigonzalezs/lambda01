package wabilytics.carrierChanged

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "carrier")
data class Carrier (
        @DatabaseField var id: String? = null,
        @DatabaseField var store: String? = null,
        @DatabaseField var enabled: Boolean = false,
        @DatabaseField var userDisabled: Boolean = false) {

    constructor() : this(null, null, false, false) {
        // ORMLite needs a no-arg constructor
    }
}