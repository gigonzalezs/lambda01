package wabilytics

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.stream.Collectors

/**
 * Handler for requests to Lambda function.
 */
class CarrierChangedFunction : RequestHandler<Any?, Any> {
    override fun handleRequest(input: Any?, context: Context): Any {
        initialize(context)
        val logger = context.logger
        logger.log("CarrierChangedFunction invoked\r\n")
        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"
        headers["X-Custom-Header"] = "application/json"
        return try {
            val pageContents = getPageContents("https://checkip.amazonaws.com")
            val output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents)
            val eventos = Eventos(output)
            accountDao!!.create(eventos)
            GatewayResponse(output, headers, 200)
        } catch (e: Exception) {
            logger.log(String.format("Error of Type %s: %s", e.javaClass.name, e.message))
            GatewayResponse("{}", headers, 500)
        }
    }

    @Throws(IOException::class)
    private fun getPageContents(address: String): String {
        val url = URL(address)
        BufferedReader(InputStreamReader(url.openStream())).use { br -> return br.lines().collect(Collectors.joining(System.lineSeparator())) }
    }

    companion object {
        private var accountDao: Dao<Eventos, String>? = null
        private fun initialize(context: Context) {
            val logger = context.logger
            if (accountDao != null) return
            logger.log("initializing function...\r\n")
            val databaseUrl = System.getenv().getOrDefault("db.url", "jdbc:h2:file:~/test;DB_CLOSE_ON_EXIT=FALSE")
            logger.log(String.format("Database URL: %s.\r\n", databaseUrl))
            val username = System.getenv()["db.username"]
            val password = System.getenv()["db.password"]
            try {
                val connectionSource: ConnectionSource = JdbcConnectionSource(databaseUrl, username, password)
                accountDao = DaoManager.createDao(connectionSource, Eventos::class.java)
                TableUtils.createTableIfNotExists(connectionSource, Eventos::class.java)
                logger.log("function initialization done.\r\n")
            } catch (e: Exception) {
                logger.log(String.format("Error of Type %s: %s", e.javaClass.name, e.message))
                e.printStackTrace()
            }
        }
    }
}