package wabilytics

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.support.ConnectionSource

object Datasource {
    private var logger: LambdaLogger? = null
    private var _initialized = false
    private var _createTablesIfNotExist = false
    private var _connectionSource: ConnectionSource? = null
    private var _defaultDatabaseUrl: String = ""

    val initialized: Boolean
        get() = _initialized

    val connectionSource: ConnectionSource?
        get() = _connectionSource

    val createTablesIfNotExist: Boolean
        get() = _createTablesIfNotExist

    fun initialize(context: Context) {
        if (_initialized) return
        logger = context.logger
        setupConnectionSource()
        _initialized = true
    }
    fun initializeWithParams(context: Context, createTablesIfNotExist: Boolean, defaultDatabaseUrl: String) {
        _createTablesIfNotExist = createTablesIfNotExist
        _defaultDatabaseUrl = defaultDatabaseUrl
        initialize(context)

    }
    private fun setupConnectionSource() {
        val databaseUrl = System.getenv().getOrDefault("DB_URL", _defaultDatabaseUrl)
        logger?.log(String.format("Database URL: %s.\r\n", databaseUrl))
        val username = System.getenv()["DB_USERNAME"]
        val password = System.getenv()["DB_PASSWORD"]
        if (!_createTablesIfNotExist)
            _createTablesIfNotExist = System.getenv().getOrDefault("DB_CREATE_TABLES","false").toBoolean()
        try {
            _connectionSource = JdbcConnectionSource(databaseUrl, username, password)
            logger?.log("datasource initialization done.\r\n")
        } catch (e: Exception) {
            logger?.log("datasource initialization fail!.\r\n")
            logger?.log(String.format("Error of Type %s: %s", e.javaClass.name, e.message))
            e.printStackTrace()
        }
    }
}