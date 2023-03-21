package org.example.config.httpserver;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.List;
import org.example.endpoints.Endpoint;

public interface CustomHttpServer {
  void start(List<Endpoint> handlers)  throws IOException;
  void configureHttpHandler(HttpServer server, List<Endpoint> handlers);
}
