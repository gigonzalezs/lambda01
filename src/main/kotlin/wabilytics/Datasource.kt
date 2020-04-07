package wabilytics

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils

object Datasource {
    private var _initialized = false
    private var _createTablesIfNotExist = false
    private var logger: LambdaLogger? = null
    private var _connectionSource: ConnectionSource? = null
    private var _accountDao: Dao<Eventos, String>? = null

    val connectionSource: ConnectionSource?
        get() = _connectionSource

    val createTablesIfNotExist: Boolean
        get() = _createTablesIfNotExist

    val accountDao: Dao<Eventos, String>?
        get() = _accountDao

    fun initialize(context: Context) {
        if (_initialized) return
        logger = context.logger
        setupConnectionSource()
        setupAccountDAO()
    }
    private fun setupConnectionSource() {
        val databaseUrl = System.getenv().getOrDefault("db.url", "jdbc:h2:file:~/test;DB_CLOSE_ON_EXIT=FALSE")
        logger?.log(String.format("Database URL: %s.\r\n", databaseUrl))
        val username = System.getenv()["db.username"]
        val password = System.getenv()["db.password"]
        _createTablesIfNotExist = System.getenv().getOrDefault("db.createTables","false").toBoolean()
        try {
            _connectionSource = JdbcConnectionSource(databaseUrl, username, password)
            logger?.log("datasource initialization done.\r\n")
            _initialized = true
        } catch (e: Exception) {
            logger?.log("datasource initialization fail!.\r\n")
            logger?.log(String.format("Error of Type %s: %s", e.javaClass.name, e.message))
            e.printStackTrace()
        }
    }

    private fun setupAccountDAO() {
        _accountDao = DaoManager.createDao(connectionSource, Eventos::class.java)
        if (_createTablesIfNotExist) {
            TableUtils.createTableIfNotExists(connectionSource, Eventos::class.java)
        }
    }
}