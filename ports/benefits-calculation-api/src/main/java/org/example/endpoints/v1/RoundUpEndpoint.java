package org.example.endpoints.v1;

import com.google.inject.Inject;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.HashMap;
import kotlin.Pair;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.endpoints.Endpoint;
import org.example.factories.interfaces.Factory;
import org.example.handlers.CommandHandler;
import org.example.responses.HttpResponseWrapper;

public class RoundUpEndpoint implements Endpoint {
  private static final String ROUND_UP_ENDPOINT_URN = "/v1/benefits/round-up";
  private final CommandHandler<RoundUpWeeklyCommand, Boolean> commandHandler;
  private final Factory<RoundUpWeeklyCommand, Pair<String, HashMap<String, String>>> commandFactory;
  @Inject
  public RoundUpEndpoint(CommandHandler<RoundUpWeeklyCommand, Boolean> commandHandler,
      Factory<RoundUpWeeklyCommand, Pair<String, HashMap<String, String>>> commandFactory) {
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
          var params = this.queryToMap(exchange.getRequestURI().getQuery());
          var command = this.commandFactory.create(new Pair<>(exchange.getRequestHeaders().getFirst("Authorization"), params));
          var success = this.commandHandler.handle(command);
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

  private HashMap<String, String> queryToMap(String query) {
    if(query == null) {
      return null;
    }
    HashMap<String, String> result = new HashMap<>();
    for (String param : query.split("&")) {
      String[] entry = param.split("=");
      if (entry.length > 1) {
        result.put(entry[0], entry[1]);
      }else{
        result.put(entry[0], "");
      }
    }
    return result;
  }

  @Override
  public String getEndpointURN() {
    return ROUND_UP_ENDPOINT_URN;
  }
}
