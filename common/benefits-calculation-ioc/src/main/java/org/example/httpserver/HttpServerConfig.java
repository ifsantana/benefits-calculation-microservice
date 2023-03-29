package org.example.httpserver;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.example.BenefitsCalculationMicroservice;
import org.example.endpoints.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerConfig implements CustomHttpServer {
  private static final Logger logger = LoggerFactory.getLogger(HttpServerConfig.class);
  private HttpServer server;
  private Executor executor;

  @Override
  public void start(List<Endpoint> handlers)  throws IOException {
    this.server = HttpServer.create(new InetSocketAddress(8000), 0);
    this.configureHttpHandler(this.server, handlers);
    this.executor = Executors.newFixedThreadPool(this.getOptimalNumberOfCores());
    this.server.setExecutor(this.executor);
    this.server.start();
    logger.info(MessageFormat.format("Web server started and listening on {0} ...", server.getAddress()));
  }

  @Override
  public void configureHttpHandler(HttpServer server, List<Endpoint> handlers) {
    logger.info("Starting web server...");
    logger.info("Configuring endpoints...");
    for(Endpoint handler : handlers) {
      logger.info(MessageFormat.format("...publishing {0} endpoint.", handler.getEndpointURN()));
      server.createContext(handler.getEndpointURN(), handler);
    }
  }

  private int getOptimalNumberOfCores() {
    return Runtime.getRuntime().availableProcessors();
  }
}
