package org.example.endpoints.v1;

import com.google.inject.Inject;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import org.example.endpoints.Endpoint;
import org.example.handlers.CommandHandler;
import org.example.responses.HttpResponseWrapper;

public class RoundUpEndpoint implements Endpoint {
  private static final String ROUND_UP_ENDPOINT_URN = "/v1/benefits/round-up";
  private final CommandHandler<String, Boolean> commandHandler;

  @Inject
  public RoundUpEndpoint(CommandHandler<String, Boolean> commandHandler) {
    this.commandHandler = commandHandler;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if(!exchange.getRequestHeaders().containsKey("Authorization")){
      HttpResponseWrapper.http404(exchange);
    } else {
      if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        try {
          var success = this.commandHandler.handle(exchange.getRequestHeaders().getFirst("Authorization"));

          if(success)
            HttpResponseWrapper.http200(exchange);
          else
            HttpResponseWrapper.http500(exchange);
        } catch (Exception e) {
          HttpResponseWrapper.http500(exchange);
        }
      } else {
        HttpResponseWrapper.http405(exchange);
      }
    }
  }

  @Override
  public String getEndpointURN() {
    return ROUND_UP_ENDPOINT_URN;
  }
}
