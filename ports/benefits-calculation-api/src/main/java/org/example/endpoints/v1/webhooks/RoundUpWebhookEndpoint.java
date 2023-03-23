package org.example.endpoints.v1.webhooks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import org.example.endpoints.Endpoint;
import org.example.endpoints.v1.RoundUpEndpoint.FeedItem;

public class RoundUpWebhookEndpoint implements Endpoint {
  private ObjectMapper objectMapper = new ObjectMapper();
  private static final String ROUND_UP_WEBHOOK_ENDPOINT_URN = "/v1/benefits/webhook/feed-item";

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      try {
        var body = this.objectMapper.readValue(exchange.getRequestBody(), AccountHolderWebhookDispatchFeedItem.class);
        exchange.sendResponseHeaders(200, "webhook ok".getBytes().length);
      } catch (IOException e) {
        throw new IOException("reason: ", e);
      }
      OutputStream outputStream = exchange.getResponseBody();
      outputStream.write("webhook ok".getBytes());
      outputStream.close();
    } else {
      exchange.sendResponseHeaders(405, "Method Not Allowed".getBytes().length);
      OutputStream outputStream = exchange.getResponseBody();
      outputStream.write("Method Not Allowed".getBytes());
      outputStream.close();
    }
  }

  public record AccountHolderWebhookDispatchFeedItem(String webhookEventUid, String eventTimestamp, String accountHolderUid, FeedItem content) implements
      Serializable {
  }
  @Override
  public String getEndpointURN() {
    return ROUND_UP_WEBHOOK_ENDPOINT_URN;
  }
}
