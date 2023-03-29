package org.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.util.List;
import org.example.endpoints.Endpoint;
import org.example.endpoints.v1.RoundUpEndpoint;
import org.example.factories.interfaces.RoundUpWeeklyCommandFactory;
import org.example.handlers.interfaces.RoundUpRealtimeCommandHandler;
import org.example.handlers.interfaces.RoundUpWeeklyCommandHandler;
import org.example.httpserver.HttpServerConfig;
import org.example.webhooks.RoundUpWebhookEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenefitsCalculationMicroservice {
  private static final Logger logger = LoggerFactory.getLogger(BenefitsCalculationMicroservice.class);
  public static void main(String[] args) throws IOException {
    logger.info("                                                                                                                               \n"
        + ",-----.                       ,---.,--.  ,--.           ,-----.       ,--.             ,--.          ,--.  ,--.                \n"
        + "|  |) /_ ,---. ,--,--, ,---. /  .-'`--',-'  '-. ,---.  '  .--./,--,--.|  |,---.,--.,--.|  | ,--,--.,-'  '-.`--' ,---. ,--,--,  \n"
        + "|  .-.  \\ .-. :|      \\ .-. :|  `-,,--.'-.  .-'(  .-'  |  |   ' ,-.  ||  | .--'|  ||  ||  |' ,-.  |'-.  .-',--.| .-. ||      \\ \n"
        + "|  '--' |   --.|  ||  \\   --.|  .-'|  |  |  |  .-'  `) '  '--'\\ '-'  ||  \\ `--.'  ''  '|  |\\ '-'  |  |  |  |  |' '-' '|  ||  | \n"
        + "`------' `----'`--''--'`----'`--'  `--'  `--'  `----'   `-----'`--`--'`--'`---' `----' `--' `--`--'  `--'  `--' `---' `--''--' "
        + "\"by Italo Santana\"");
    HttpServerConfig serverConfig = new HttpServerConfig();
    serverConfig.start(getPublishedEndpoints());
  }

  public static List<Endpoint> getPublishedEndpoints() {
    Injector injector = Guice.createInjector(new InjectionManager());
    return List.of(new RoundUpEndpoint(injector.getInstance(RoundUpWeeklyCommandHandler.class),
            injector.getInstance(RoundUpWeeklyCommandFactory.class)
        ),
        new RoundUpWebhookEndpoint(injector.getInstance(RoundUpRealtimeCommandHandler.class))
    );
  }
}
