package org.example.config.httpserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.example.endpoints.Endpoint;

public class HttpServerConfig implements CustomHttpServer {
  private HttpServer server;
  private static final int DEFAULT_CUSTOM_THREAD_POOL = 1;
  private Executor executor;

  @Override
  public void start(List<Endpoint> handlers)  throws IOException {
    this.server = HttpServer.create(new InetSocketAddress(8000), 0);
    this.configureHttpHandler(this.server, handlers);
    this.executor = Executors.newFixedThreadPool(DEFAULT_CUSTOM_THREAD_POOL);
    this.server.setExecutor(this.executor);
    this.server.start();
  }

  @Override
  public void configureHttpHandler(HttpServer server, List<Endpoint> handlers) {
    for(Endpoint handler : handlers)
      server.createContext( handler.getEndpointURN(), handler);
  }
}
