package wabilytics.carrierChanged

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.table.TableUtils
import wabilytics.Datasource

object CarrierDao {
    private var logger: LambdaLogger? = null
    private var _initialized = false
    private var _dao: Dao<Carrier, String>? = null

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
        _dao = DaoManager.createDao(Datasource.connectionSource, Carrier::class.java)
        if (Datasource.createTablesIfNotExist) {
            TableUtils.createTableIfNotExists(Datasource.connectionSource, Carrier::class.java)
        }
    }

    fun save(carrier: Carrier) {
        _dao?.create(carrier)
    }
}