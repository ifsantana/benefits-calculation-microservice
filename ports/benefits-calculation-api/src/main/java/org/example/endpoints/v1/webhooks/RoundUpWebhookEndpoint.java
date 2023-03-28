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
        /**
         * 1 - get authorizationid: https://oauth-sandbox.starlingbank.com/partner-developer/api/v1/available-application/ahO9mdmqPi5WPeO9P7Xa/qr-code
         * 2 - get code: https://developer.starlingbank.com/partner-developer/api/v1/application-authorisation/ce0a2adb-cb42-4a75-a7a4-d33986cd4d1e get code on redirect url
         * 3 - get token: https://api-sandbox.starlingbank.com/oauth/access-token
         */
        var event = this.objectMapper.readValue(exchange.getRequestBody(), AccountHolderWebhookDispatchFeedItem.class);
        var success = this.commandHandler.handle(event);
        if(success)
          HttpResponseWrapper.http200(exchange);
      } catch (IOException e) {
        HttpResponseWrapper.http500(exchange, e.getMessage());
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
