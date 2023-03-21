package org.example.endpoints.v1;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import org.example.endpoints.Endpoint;

public class WebhookEndpoint implements Endpoint {
  private static final String ROUND_UP_WEBHOOK_ENDPOINT_URN = "/v1/benefits/webhook/round-up";

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      exchange.sendResponseHeaders(200, "webhook ok".getBytes().length);
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

  @Override
  public String getEndpointURN() {
    return ROUND_UP_WEBHOOK_ENDPOINT_URN;
  }
}
