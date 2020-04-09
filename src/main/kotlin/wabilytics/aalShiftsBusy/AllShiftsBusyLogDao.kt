package wabilytics.aalShiftsBusy

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.fasterxml.jackson.databind.JsonNode
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.table.TableUtils
import wabilytics.Datasource
import java.time.Clock
import java.util.*

object AllShiftsBusyLogDao {
    private var logger: LambdaLogger? = null
    private var _initialized = false
    private var _dao: Dao<AllShiftsBusyLog, String>? = null
    private val clock: Clock = Clock.systemDefaultZone()

    val initialized: Boolean
        get() = _initialized

    fun initialize(context: Context) {
        if (_initialized) return
        logger = context.logger
        Datasource.initialize(context)
        setupDAO()
        _initialized = true;
    }

    private fun setupDAO() {
        _dao = DaoManager.createDao(Datasource.connectionSource, AllShiftsBusyLog::class.java)
        if (Datasource.createTablesIfNotExist) {
            TableUtils.createTableIfNotExists(Datasource.connectionSource, AllShiftsBusyLog::class.java)
        }
    }

    fun save(log: AllShiftsBusyLog) {
        _dao?.create(log)
    }
}

fun JsonNode.toAllShiftsBusyLog() = AllShiftsBusyLog(
        id = UUID.fromString(this["id"].asText()),
        customerId = this["customerId"].asText(),
        latitude = this["latitude"].asDouble(),
        longitude = this["longitude"].asDouble()
)