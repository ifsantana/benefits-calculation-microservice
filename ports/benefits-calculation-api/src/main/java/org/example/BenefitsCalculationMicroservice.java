package org.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.util.List;
import org.example.config.httpserver.HttpServerConfig;
import org.example.endpoints.Endpoint;
import org.example.endpoints.v1.RoundUpEndpoint;
import org.example.endpoints.v1.webhooks.RoundUpWebhookEndpoint;
import org.example.handlers.interfaces.RoundUpWeeklyCommandHandler;

public class BenefitsCalculationMicroservice {
  public static void main(String[] args) throws IOException {
    HttpServerConfig serverConfig = new HttpServerConfig();
    serverConfig.start(getPublishedEndpoints());
  }

  public static List<Endpoint> getPublishedEndpoints() {
    Injector injector = Guice.createInjector(new InjectionManager());
    return List.of(
        new RoundUpEndpoint(injector.getInstance(RoundUpWeeklyCommandHandler.class)),
        new RoundUpWebhookEndpoint()
    );
  }
}
