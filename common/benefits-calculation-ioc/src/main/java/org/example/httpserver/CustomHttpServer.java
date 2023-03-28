package org.example.httpserver;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.List;
import org.example.endpoints.Endpoint;

public interface CustomHttpServer {

  /**
   * This method initializes the web server and their handlers (endpoints).
   *
   * @param - handlers
   * @throws IOException
   */
  void start(List<Endpoint> handlers) throws IOException;

  /**
   * This method configures allows the http handlers/endpoints configuration.
   *
   * @param - server
   * @param - handlers
   */
  void configureHttpHandler(HttpServer server, List<Endpoint> handlers);
}
