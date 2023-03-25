package org.example.endpoints.v1.webhooks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.example.endpoints.Endpoint;
import org.example.events.AccountHolderWebhookDispatchFeedItem;
import org.example.responses.HttpResponseWrapper;

public class RoundUpWebhookEndpoint implements Endpoint {
  private ObjectMapper objectMapper = new ObjectMapper();
  private static final String ROUND_UP_WEBHOOK_ENDPOINT_URN = "/v1/benefits/webhook/feed-item";

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      try {
        var body = this.objectMapper.readValue(exchange.getRequestBody(), AccountHolderWebhookDispatchFeedItem.class);
        HttpResponseWrapper.http200(exchange);
      } catch (IOException e) {
        throw new IOException("reason: ", e);
      }
    } else {
      HttpResponseWrapper.http405(exchange);
    }
  }

  @Override
  public String getEndpointURN() {
    return ROUND_UP_WEBHOOK_ENDPOINT_URN;
  }
}
