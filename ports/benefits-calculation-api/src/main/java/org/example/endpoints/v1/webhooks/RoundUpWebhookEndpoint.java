package org.example.endpoints.v1.webhooks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.example.endpoints.Endpoint;
import org.example.events.AccountHolderWebhookDispatchFeedItem;
import org.example.handlers.CommandHandler;
import org.example.responses.HttpResponseWrapper;

public class RoundUpWebhookEndpoint implements Endpoint {
  private static final String ROUND_UP_WEBHOOK_ENDPOINT_URN = "/v1/benefits/webhook/feed-item";
  private final ObjectMapper objectMapper;
  private final CommandHandler<AccountHolderWebhookDispatchFeedItem, Boolean> commandHandler;

  @Inject
  public RoundUpWebhookEndpoint(CommandHandler<AccountHolderWebhookDispatchFeedItem, Boolean> commandHandler) {
    this.commandHandler = commandHandler;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      try {
        var event = this.objectMapper.readValue(exchange.getRequestBody(), AccountHolderWebhookDispatchFeedItem.class);
        var success = this.commandHandler.handle(event);
        if(success)
          HttpResponseWrapper.http200(exchange);
        else
          HttpResponseWrapper.http500(exchange);
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
