package org.example.endpoints.v1;


import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import kotlin.Pair;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.commands.RoundUpWeeklyCommand;
import org.example.factories.interfaces.RoundUpWeeklyCommandFactory;
import org.example.handlers.interfaces.RoundUpWeeklyCommandHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoundUpEndpointIT {
  @Mock
  private RoundUpWeeklyCommandHandler commandHandler;
  @Mock
  private RoundUpWeeklyCommandFactory commandFactory;
  @InjectMocks
  private RoundUpEndpoint roundUpEndpoint;
  static OkHttpClient client;
  static InetSocketAddress address;
  static HttpServer httpServer;

  @BeforeAll
  static void beforeAll() throws IOException {
    address = new InetSocketAddress(8000);
    httpServer = HttpServer.create(address, 0);
    httpServer.start();
    client = new OkHttpClient();
  }

  @BeforeEach
  void setup() throws IOException {
    httpServer.createContext(this.roundUpEndpoint.getEndpointURN(), this.roundUpEndpoint);
  }

  @AfterAll
  static void after() {
    httpServer.stop(1);
  }

  @Test
  void shouldReturn200() throws IOException {
    var command = new RoundUpWeeklyCommand("Bearer fake.token","2023-03-27T00:00:00.000Z", "2023-03-28T12:48:00.000Z");
    when(this.commandFactory.create(any())).thenReturn(command);
    when(this.commandHandler.handle(command)).thenReturn(new Pair<>(true, "Success"));
    Request request = new Request.Builder()
        .url("http://localhost:8000/v1/benefits/round-up?minTransactionTimestamp=2023-03-27T00:00:00.000Z&maxTransactionTimestamp=2023-03-28T12:48:00.000Z")
        .method("POST", RequestBody.create("", MediaType.parse("application/json")))
        .addHeader("Authorization", "Bearer fake.token")
        .build();
    Response response = client.newCall(request).execute();
    assertThat(response.code()).isEqualTo(200);
  }

  @Test
  void shouldReturn404() throws IOException {
    Request request = new Request.Builder()
        .url("http://localhost:8000/v1/benefits/round-up")
        .build();
    Response response = client.newCall(request).execute();
    assertThat(response.code()).isEqualTo(404);
  }

  @Test
  void shouldReturn405() throws IOException {
    Request request = new Request.Builder()
        .addHeader("Authorization", "Bearer fake.token")
        .url("http://localhost:8000/v1/benefits/round-up?minTransactionTimestamp=2023-03-27T00:00:00.000Z&maxTransactionTimestamp=2023-03-28T12:48:00.000Z")
        .build();
    Response response = client.newCall(request).execute();
    assertThat(response.code()).isEqualTo(405);
    assertThat(response.message()).isEqualTo("Method Not Allowed");
  }
}
