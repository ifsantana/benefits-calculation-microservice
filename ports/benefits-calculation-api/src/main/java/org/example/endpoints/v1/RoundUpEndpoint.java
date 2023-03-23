package org.example.endpoints.v1;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
      var savingGoals = this.testingGetSavings(token, account);
      var totalToTransferToGoals = this.calculateRoundUp(feedItems);
      var transferResult = this.testingAddMoneyToSaving(token, account, savingGoals, totalToTransferToGoals);
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

  public SavingGoal testingGetSavings(String token, AccountResponse accountResponse) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    HttpUrl httpUrl = this.buildSavingGoalsUrl(accountResponse.accountUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.client.newCall(request).execute().body();
    try {
      GetSavingGoalsResponse savingGoals = objectMapper.readValue(body.string(), GetSavingGoalsResponse.class);

      if(savingGoals.savingsGoalList().size() == 0) {
       var createdSavingResult = this.testingCreateSaving(token, accountResponse);
       var savingGoal = this.testingGetSavingGoal(token, accountResponse, createdSavingResult.savingsGoalUid);
       return savingGoal;
      }

      return savingGoals.savingsGoalList.stream().min(Comparator.comparingInt(SavingGoal::savedPercentage)).get();
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  public SavingGoal testingGetSavingGoal(String token, AccountResponse accountResponse, String savingGoalUid) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    HttpUrl httpUrl = this.buildSavingGoalByIdUrl(accountResponse.accountUid, savingGoalUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.client.newCall(request).execute().body();
    try {
      SavingGoal savingGoal = objectMapper.readValue(body.string(), SavingGoal.class);
      return savingGoal;
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  public CreateSavingGoalResponse testingCreateSaving(String token, AccountResponse accountResponse) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    var tripToParisGoal = new CreateSavingGoalRequest("Trip to Paris", "GBP", new SavingGoalTarget("GBP", "1000"));
    var requestBody = objectMapper.writeValueAsString(tripToParisGoal);
    HttpUrl httpUrl = this.buildSavingGoalsUrl(accountResponse.accountUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .put(RequestBody.create(requestBody, MediaType.parse("application/json")))
        .build();
    ResponseBody body = this.client.newCall(request).execute().body();
    try {
      /**
       * ERROR THROWING HERE
       */
      CreateSavingGoalResponse savingGoalsResult = objectMapper.readValue(body.string(), CreateSavingGoalResponse.class);
      return savingGoalsResult;
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  public SavingGoalTransferResponse testingAddMoneyToSaving(String token, AccountResponse accountResponse, SavingGoal savingGoal, Integer amountToAdd) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    var topUpRequest = new TopUpRequest(new Amount(savingGoal.totalSaved.currency, amountToAdd));
    var requestBody = objectMapper.writeValueAsString(topUpRequest);
    HttpUrl httpUrl = this.buildTopUpSavingUrl(accountResponse.accountUid, savingGoal.savingsGoalUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .put(RequestBody.create(requestBody, MediaType.parse("application/json")))
        .build();
    ResponseBody body = this.client.newCall(request).execute().body();
    try {
      /**
       * ERROR THROWING HERE
       */
      SavingGoalTransferResponse savingGoalTransferResult = objectMapper.readValue(body.string(), SavingGoalTransferResponse.class);
      return savingGoalTransferResult;
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  private HttpUrl buildSavingGoalsUrl(String accountUid) {
    return new HttpUrl
        .Builder()
        .scheme("https")
        .host("api-sandbox.starlingbank.com")
        .addPathSegment("api")
        .addPathSegment("v2")
        .addPathSegment("account")
        .addPathSegment(accountUid)
        .addPathSegment("savings-goals")
        .build();
  }

  private HttpUrl buildSavingGoalByIdUrl(String accountUid, String savingGoalUid) {
    return new HttpUrl
        .Builder()
        .scheme("https")
        .host("api-sandbox.starlingbank.com")
        .addPathSegment("api")
        .addPathSegment("v2")
        .addPathSegment("account")
        .addPathSegment(accountUid)
        .addPathSegment("savings-goals")
        .addPathSegment(savingGoalUid)
        .build();
  }

  private HttpUrl buildTopUpSavingUrl(String accountUid, String savingGoalUid) {
    return new HttpUrl
        .Builder()
        .scheme("https")
        .host("api-sandbox.starlingbank.com")
        .addPathSegment("api")
        .addPathSegment("v2")
        .addPathSegment("account")
        .addPathSegment(accountUid)
        .addPathSegment("savings-goals")
        .addPathSegment(savingGoalUid)
        .addPathSegment("add-money")
        .addPathSegment(UUID.randomUUID().toString())
        .build();
  }

  /**
   * Feed Items
   */
  public record FeedItems(List<FeedItem> feedItems) implements
      Serializable {
  }

  public record FeedItem(String feedItemUid, String categoryUid, Amount amount, String direction, String spendingCategory, String country) implements
      Serializable {
  }

  public record Amount(String currency, Integer minorUnits) implements
      Serializable {
  }

  /**
   * Accounts
   */
  public record Accounts(List<AccountResponse> accounts) implements
      Serializable {
  }

  public record AccountResponse(String accountUid, String accountType, String defaultCategory, String currency, String createdAt, String name) implements
      Serializable {
  }

  /**
   * Saving Goal
   */
  public record GetSavingGoalsResponse(List<SavingGoal> savingsGoalList) implements
      Serializable {
  }

  public record SavingGoal(String savingsGoalUid, String name, SavingGoalTarget target, SavingGoalTotalSaved totalSaved, Integer savedPercentage) implements
      Serializable {
  }

  public record SavingGoalTarget(String currency, String minorUnits) implements
      Serializable {
  }

  public record SavingGoalTotalSaved(String currency, String minorUnits) implements
      Serializable {
  }

  public record CreateSavingGoalRequest(String name, String currency, SavingGoalTarget target) implements
      Serializable {
  }

  public record CreateSavingGoalResponse(String savingsGoalUid, boolean success) implements
      Serializable {
  }

  public record SavingGoalTransferResponse(String transferUid, boolean success) implements
      Serializable {
  }

  /**
   * TOPUP
   */
  public record TopUpRequest(Amount amount) implements
      Serializable {
  }

  public Integer calculateRoundUp(List<FeedItem> txnList) {
    Double total = Double.MIN_VALUE;
    for(FeedItem item : txnList) {
      var initValue1 = new BigDecimal(item.amount.minorUnits.toString()).movePointLeft(2);
      var roundup1 = initValue1.round(new MathContext(initValue1.scale(), RoundingMode.UP));
      var roundUpResult = roundup1.subtract(initValue1);
      total += roundUpResult.doubleValue();
    }
    Double result = total*100;
    return  result.intValue();
  }

  @Override
  public String getEndpointURN() {
    return ROUND_UP_ENDPOINT_URN;
  }
}
