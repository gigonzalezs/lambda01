package wabilytics;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import wabilytics.aalShiftsBusy.AllShiftsBusyFunction;
import wabilytics.carrierChanged.CarrierChangedFunction;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class AllShiftsBusyFunctionTest {
  @Test
  public void successfulResponse() throws IOException {
    ContextMock context = new ContextMock();
    Datasource.INSTANCE.initializeWithParams(context, true, "jdbc:h2:file:~/test;DB_CLOSE_ON_EXIT=FALSE");
    AllShiftsBusyFunction fn = new AllShiftsBusyFunction();
    /*

{"Records":
 [
  {
    "EventSource": "aws:sns",
    "EventVersion": "1.0",
    "EventSubscriptionArn": "arn:aws:sns:us-east-1:835328222444:eventos:e8882169-78f9-47b4-862c-c673afbae204",
    "Sns": {
       "Type": "Notification",
       "MessageId": "24019796-9cac-5682-8b41-c09ed6e702ba",
       "TopicArn": "arn:aws:sns:us-east-1:835328222444:eventos",
       "Subject": "carrierChanged",
       "Message": {
          "customerId": "fc2d8434-7d9f-11ea-bc55-0242ac130003",
          "latitude": -66.345,
          "longitude": -10.456
       },
       "Timestamp": "2020-04-08T21:54:19.960Z",
       "SignatureVersion": "1",
       "Signature": "ECsvTsj3gfW93Qm+C06LO/AzrAxvXhpBNR4f999eaZRJiBCe+837gha9A::",
       "SigningCertUrl": "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-a86cb10b4e1f29c941702d737128f7b6.pem",
       "UnsubscribeUrl": "https://sns.us-east-1.amazonaws.com/?Action:Unsubscribe&SubscriptionArn:arn:aws:sns:us-east-1:835328222444:eventos:e8882169-78f9-47b4-862c-c673afbae204",
       "MessageAttributes": {}
    }
  }
 ]
}
     */
    JsonNode payload = StringToJsonNode("{\"Records\":[{\"EventSource\":\"aws:sns\",\"EventVersion\":\"1.0\",\"EventSubscriptionArn\":\"arn:aws:sns:us-east-1:835328222444:eventos:e8882169-78f9-47b4-862c-c673afbae204\",\"Sns\":{\"Type\":\"Notification\",\"MessageId\":\"24019796-9cac-5682-8b41-c09ed6e702ba\",\"TopicArn\":\"arn:aws:sns:us-east-1:835328222444:eventos\",\"Subject\":\"carrierChanged\",\"Message\":{\"customerId\":\"fc2d8434-7d9f-11ea-bc55-0242ac130003\",\"latitude\":-66.345,\"longitude\":-10.456},\"Timestamp\":\"2020-04-08T21:54:19.960Z\",\"SignatureVersion\":\"1\",\"Signature\":\"ECsvTsj3gfW93Qm+C06LO/AzrAxvXhpBNR4f999eaZRJiBCe+837gha9A::\",\"SigningCertUrl\":\"https://sns.us-east-1.amazonaws.com/SimpleNotificationService-a86cb10b4e1f29c941702d737128f7b6.pem\",\"UnsubscribeUrl\":\"https://sns.us-east-1.amazonaws.com/?Action:Unsubscribe&SubscriptionArn:arn:aws:sns:us-east-1:835328222444:eventos:e8882169-78f9-47b4-862c-c673afbae204\",\"MessageAttributes\":{}}}]}");
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object>  payloadAsMap = mapper.convertValue(payload, new TypeReference<Map<String, Object>>(){});
    String result =  fn.handleRequest(payloadAsMap, context);
    assertEquals(result, "OK");
  }

  private static JsonNode StringToJsonNode(String jsonString) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser parser = factory.createParser(jsonString);
    JsonNode actualObj = mapper.readTree(parser);
    return actualObj;
  }
}
