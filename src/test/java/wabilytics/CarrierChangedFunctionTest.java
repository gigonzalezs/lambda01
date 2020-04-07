package wabilytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import wabilytics.carrierChanged.CarrierChangedFunction;

import java.io.IOException;

public class CarrierChangedFunctionTest {
  @Test
  public void successfulResponse() throws IOException {
    ContextMock context = new ContextMock();
    Datasource.INSTANCE.initializeWithParams(context, true, "jdbc:h2:file:~/test;DB_CLOSE_ON_EXIT=FALSE");
    CarrierChangedFunction fn = new CarrierChangedFunction();
    JsonNode payload = StringToJsonNode("{\"k1\":\"v1\",\"k2\":\"v2\"}");
    EventRequest request = new EventRequest("subject", payload);
    GatewayResponse result = (GatewayResponse) fn.handleRequest(request, context);
    assertEquals(result.getStatusCode(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json");
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"hello world\""));
    assertTrue(content.contains("\"location\""));
  }

  private static JsonNode StringToJsonNode(String jsonString) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser parser = factory.createParser(jsonString);
    JsonNode actualObj = mapper.readTree(parser);
    return actualObj;
  }
}
