package org.example.responses;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponseWrapper {
  private HttpResponseWrapper() {}

  /**
   *
   * @param - exchange
   * @param - httpResponse
   * @throws IOException
   */
  public static void httpResponse(HttpExchange exchange, HttpResponse httpResponse) throws IOException  {
    exchange.sendResponseHeaders(httpResponse.code(), httpResponse.message().getBytes().length);
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write(httpResponse.message().getBytes());
    outputStream.close();
  }

  /**
   *
   * @param - exchange
   * @throws IOException
   */
  public static void http404(HttpExchange exchange) throws IOException {
    httpResponse(exchange, new HttpResponse(404, "Forbidden"));
  }

  /**
   *
   * @param - exchange
   * @throws IOException
   */
  public static void http200(HttpExchange exchange) throws IOException {
    httpResponse(exchange, new HttpResponse(200, "OK"));
  }

  /**
   *
   * @param - exchange
   * @throws IOException
   */
  public static void http405(HttpExchange exchange) throws IOException {
    httpResponse(exchange, new HttpResponse(405, "Method Not Allowed"));
  }

  /**
   *
   * @param - exchange
   * @throws IOException
   */
  public static void http422(HttpExchange exchange, String message) throws IOException {
    httpResponse(exchange, new HttpResponse(422, message));
  }

  /**
   *
   * @param - exchange
   * @throws IOException
   */
  public static void http500(HttpExchange exchange, String message) throws IOException {
    httpResponse(exchange, new HttpResponse(500, message));
  }
}
