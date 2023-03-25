package org.example.endpoints.v1;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
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
import org.example.handlers.CommandHandler;
import org.example.requests.Amount;
import org.example.requests.savings.CreateSavingGoalRequest;
import org.example.requests.savings.TopUpRequest;
import org.example.responses.HttpResponse;
import org.example.responses.accounts.Account;
import org.example.responses.accounts.AccountsResponse;
import org.example.responses.savings.CreateSavingGoalResponse;
import org.example.responses.savings.GetSavingGoalsResponse;
import org.example.responses.savings.SavingGoal;
import org.example.responses.savings.SavingGoalTransferResponse;
import org.example.responses.txnfeed.FeedItem;
import org.example.responses.txnfeed.FeedItemsResponse;

public class RoundUpEndpoint implements Endpoint {
  private final OkHttpClient client = new OkHttpClient();
  private static final String ROUND_UP_ENDPOINT_URN = "/v1/benefits/round-up";

  private final CommandHandler<String, Void> commandHandler;

  @Inject
  public RoundUpEndpoint(CommandHandler<String, Void> commandHandler) {
    this.commandHandler = commandHandler;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if(!exchange.getRequestHeaders().containsKey("Authorization")){
      HttpResponse.http404(exchange);
    } else {
      if(exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        try {
          this.commandHandler.handle(exchange.getRequestHeaders().get("Authorization").get(0));
//          var token = exchange.getRequestHeaders().get("Authorization").get(0);
//          var account = this.testingGetAccount(token);
//          var feedItems = this.testingGetTxnFeedItems(token, account);
//          var savingGoals = this.testingGetSavings(token, account);
//          var totalToTransferToGoals = this.calculateRoundUp(feedItems);
//          var transferResult = this.testingAddMoneyToSaving(token, account, savingGoals, totalToTransferToGoals);
          HttpResponse.http200(exchange);
        } catch (Exception e) {
          HttpResponse.http500(exchange);
        }
      } else {
        HttpResponse.http405(exchange);
      }
    }
  }

  public Account testingGetAccount(String token) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    Request request = new Request.Builder()
        .url(" https://api-sandbox.starlingbank.com/api/v2/accounts")
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.client.newCall(request).execute().body();
    try {
      AccountsResponse accountResponse = objectMapper.readValue(body.string(),
          AccountsResponse.class);

      return accountResponse.accounts().get(0);
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  public List<FeedItem> testingGetTxnFeedItems(String token, Account accountResponse) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    HttpUrl httpUrl = this.buildTxnFeedItemsUrl(accountResponse.accountUid());
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.client.newCall(request).execute().body();
    try {
      FeedItemsResponse feedItems = objectMapper.readValue(body.string(),
          FeedItemsResponse.class);

      return feedItems.feedItems().stream().filter(feedItem -> feedItem.direction().equals("OUT")).toList();
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

  public SavingGoal testingGetSavings(String token, Account accountResponse) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    HttpUrl httpUrl = this.buildSavingGoalsUrl(accountResponse.accountUid());
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
       var savingGoal = this.testingGetSavingGoal(token, accountResponse, createdSavingResult.savingsGoalUid());
       return savingGoal;
      }

      return savingGoals.savingsGoalList().stream().min(Comparator.comparingInt(SavingGoal::savedPercentage)).get();
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  public SavingGoal testingGetSavingGoal(String token, Account accountResponse, String savingGoalUid) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    HttpUrl httpUrl = this.buildSavingGoalByIdUrl(accountResponse.accountUid(), savingGoalUid);
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

  public CreateSavingGoalResponse testingCreateSaving(String token, Account accountResponse) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    var tripToParisGoal = new CreateSavingGoalRequest("Trip to Paris", "GBP", new Amount("GBP", 1000));
    var requestBody = objectMapper.writeValueAsString(tripToParisGoal);
    HttpUrl httpUrl = this.buildSavingGoalsUrl(accountResponse.accountUid());
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

  public SavingGoalTransferResponse testingAddMoneyToSaving(String token, Account accountResponse, SavingGoal savingGoal, Integer amountToAdd) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    var topUpRequest = new TopUpRequest(new Amount(savingGoal.totalSaved().currency(), amountToAdd));
    var requestBody = objectMapper.writeValueAsString(topUpRequest);
    HttpUrl httpUrl = this.buildTopUpSavingUrl(accountResponse.accountUid(), savingGoal.savingsGoalUid());
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

  public Integer calculateRoundUp(List<FeedItem> txnList) {
    Double total = Double.MIN_VALUE;
    for(FeedItem item : txnList) {
      var initValue1 = new BigDecimal(item.amount().minorUnits().toString()).movePointLeft(2);
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
