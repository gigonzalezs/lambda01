package wabilytics;

import com.amazonaws.services.lambda.runtime.Client;
import com.amazonaws.services.lambda.runtime.ClientContext;

import java.util.HashMap;
import java.util.Map;

public class ClientContextMock implements ClientContext {
    @Override
    public Client getClient() {
        return null;
    }

    @Override
    public Map<String, String> getCustom() {
        return null;
    }

    @Override
    public Map<String, String> getEnvironment() {
        final Map<String, String> env = new HashMap<>();
        env.put("db.url", "jdbc:mysql://192.168.43.250/prueba");
        env.put("db.username", "root");
        env.put("db.password", "example");
        return env;
    }
}
