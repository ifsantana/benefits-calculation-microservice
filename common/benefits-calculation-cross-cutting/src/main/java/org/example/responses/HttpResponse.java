package org.example.responses;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
  public static void http200(HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(200, "OK".getBytes().length);
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write("OK".getBytes());
    outputStream.close();
  }

  public static void http404(HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(404, "Forbidden".getBytes().length);
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write("Forbidden".getBytes());
    outputStream.close();
  }

  public static void http405(HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(405, "Method Not Allowed".getBytes().length);
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write("Method Not Allowed".getBytes());
    outputStream.close();
  }

  public static void http500(HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(500, "Internal Server Error".getBytes().length);
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write("Internal Server Error".getBytes());
    outputStream.close();
  }
}
