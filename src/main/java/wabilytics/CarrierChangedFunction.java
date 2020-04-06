package wabilytics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Handler for requests to Lambda function.
 */
public class CarrierChangedFunction implements RequestHandler<Object, Object> {

    private static Dao<Eventos, String> accountDao;

    private static void initialize(Context context) {
        final LambdaLogger logger = context.getLogger();
        if (accountDao != null) return;
        logger.log("initializing function...\r\n");
        final String databaseUrl = System.getenv().getOrDefault("db.url", "jdbc:h2:file:~/test;DB_CLOSE_ON_EXIT=FALSE");
        /*
        Map <String, String> map = System.getenv();
        for (Map.Entry <String, String> entry: map.entrySet()) {
            if (entry.getKey().startsWith("db.")) {
                System.out.println("Variable Name:- " + entry.getKey() + " Value:- " + entry.getValue());
            }
        }
        */
        logger.log(String.format("Database URL: %s.\r\n", databaseUrl));
        final String username = System.getenv().get("db.username");
        final String password = System.getenv().get("db.password");
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl, username, password);
            accountDao = DaoManager.createDao(connectionSource, Eventos.class);
            TableUtils.createTableIfNotExists(connectionSource, Eventos.class);
            logger.log("function initialization done.\r\n");
        } catch (Exception e) {
            logger.log(String.format("Error of Type %s: %s", e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }
    }

    public Object handleRequest(final Object input, final Context context) {
        initialize(context);
        final LambdaLogger logger = context.getLogger();
        logger.log("CarrierChangedFunction invoked\r\n");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        try {
            final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            String output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents);
            final Eventos eventos = new Eventos(output);
            accountDao.create(eventos);
            return new GatewayResponse(output, headers, 200);
        } catch (Exception e) {
            logger.log(String.format("Error of Type %s: %s", e.getClass().getName(), e.getMessage()));
            return new GatewayResponse("{}", headers, 500);
        }
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
