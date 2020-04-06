package wabilytics

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "eventos")
data class Eventos(
        @DatabaseField
        var mensaje: String? = null) {

    constructor() : this(null) {
        // ORMLite needs a no-arg constructor
    }
}