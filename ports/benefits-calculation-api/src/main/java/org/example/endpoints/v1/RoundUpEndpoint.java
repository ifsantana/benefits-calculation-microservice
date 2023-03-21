package org.example.endpoints.v1;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.example.endpoints.Endpoint;

public class RoundUpEndpoint implements Endpoint {
  private final OkHttpClient client = new OkHttpClient();
  private static final String ROUND_UP_ENDPOINT_URN = "/v1/benefits/round-up";

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {
      var token = exchange.getRequestHeaders().get("Authorization").get(0);
      var account = this.testingGetAccount(token);
      var feedItems = this.testingGetTxnFeedItems(token, account);
      exchange.sendResponseHeaders(200, "OK".getBytes().length);
      OutputStream outputStream = exchange.getResponseBody();
      outputStream.write("OK".getBytes());
      outputStream.close();
    } else {
      exchange.sendResponseHeaders(405, "Method Not Allowed".getBytes().length);
      OutputStream outputStream = exchange.getResponseBody();
      outputStream.write("Method Not Allowed".getBytes());
      outputStream.close();
    }
  }

  public AccountResponse testingGetAccount(String token) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    Request request = new Request.Builder()
        .url(" https://api-sandbox.starlingbank.com/api/v2/accounts")
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.client.newCall(request).execute().body();
    try {
      Accounts accountResponse = objectMapper.readValue(body.string(),
          Accounts.class);

      return accountResponse.accounts().get(0);
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  public List<FeedItem> testingGetTxnFeedItems(String token, AccountResponse accountResponse) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    HttpUrl httpUrl = this.buildTxnFeedItemsUrl(accountResponse.accountUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.client.newCall(request).execute().body();
    try {
      FeedItems feedItems = objectMapper.readValue(body.string(),
          FeedItems.class);

      return feedItems.feedItems.stream().filter(feedItem -> feedItem.direction.equals("OUT")).toList();
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  private HttpUrl buildTxnFeedItemsUrl(String accountUid) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    return new HttpUrl
        .Builder()
        .scheme("https")
        .host("api-sandbox.starlingbank.com")
        .addPathSegment("api")
        .addPathSegment("v2")
        .addPathSegment("feed")
        .addPathSegment("account")
        .addPathSegment(accountUid)
        .addPathSegment("settled-transactions-between")
        .addQueryParameter("minTransactionTimestamp", LocalDateTime.now().minusDays(7L).format(formatter))
        .addQueryParameter("maxTransactionTimestamp", LocalDateTime.now().format(formatter))
        .build();
  }

  public record FeedItems(List<FeedItem> feedItems) implements
      Serializable {
  }


  public record FeedItem(String feedItemUid, String categoryUid, Amount amount, String direction, String spendingCategory, String country) implements
      Serializable {
  }

  public record Amount(String currency, Integer minorUnits) implements
      Serializable {
  }

  public record Accounts(List<AccountResponse> accounts) implements
      Serializable {

  }

  public record AccountResponse(String accountUid, String accountType, String defaultCategory, String currency, String createdAt, String name) implements
      Serializable {

  }

  @Override
  public String getEndpointURN() {
    return ROUND_UP_ENDPOINT_URN;
  }
}
