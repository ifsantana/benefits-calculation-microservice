package org.example.endpoints.v1;

import com.google.inject.Inject;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.HashMap;
import kotlin.Pair;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.endpoints.Endpoint;
import org.example.factories.interfaces.Factory;
import org.example.factories.interfaces.RoundUpWeeklyCommandFactory;
import org.example.handlers.CommandHandler;
import org.example.handlers.interfaces.RoundUpWeeklyCommandHandler;
import org.example.responses.HttpResponseWrapper;

public class RoundUpEndpoint implements Endpoint {
  private static final String ROUND_UP_ENDPOINT_URN = "/v1/benefits/round-up";
  private final RoundUpWeeklyCommandHandler commandHandler;
  private final RoundUpWeeklyCommandFactory commandFactory;
  @Inject
  public RoundUpEndpoint(RoundUpWeeklyCommandHandler commandHandler,
      RoundUpWeeklyCommandFactory commandFactory) {
    this.commandHandler = commandHandler;
    this.commandFactory = commandFactory;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if(!exchange.getRequestHeaders().containsKey("Authorization")){
      HttpResponseWrapper.http404(exchange);
    } else {
      if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        try {
          var token = exchange.getRequestHeaders().getFirst("Authorization");
          var params = this.queryToMap(exchange.getRequestURI().getQuery());
          if(params.isEmpty())
            HttpResponseWrapper.http400(exchange);
          var command = this.commandFactory.create(new Pair<>(token, params));
          var success = this.commandHandler.handle(command);

          if(Boolean.TRUE.equals(success.component1()))
            HttpResponseWrapper.http200(exchange);
          else
            HttpResponseWrapper.http422(exchange, success.component2());
        } catch (Exception e) {
          HttpResponseWrapper.http500(exchange, e.getMessage());
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
