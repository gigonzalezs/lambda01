package wabilytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import wabilytics.carrierChanged.CarrierChangedFunction;

public class CarrierChangedFunctionTest {
  @Test
  public void successfulResponse() {
    ContextMock context = new ContextMock();
    Datasource.INSTANCE.initializeWithParams(context, true, "jdbc:h2:file:~/test;DB_CLOSE_ON_EXIT=FALSE");
    CarrierChangedFunction fn = new CarrierChangedFunction();
    GatewayResponse result = (GatewayResponse) fn.handleRequest(null, context);
    assertEquals(result.getStatusCode(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json");
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"message\""));
    assertTrue(content.contains("\"hello world\""));
    assertTrue(content.contains("\"location\""));
  }
}
