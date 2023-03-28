package org.example;

import static org.example.constants.StarlingApiConstants.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.example.requests.Amount;
import org.example.requests.savings.CreateSavingGoalRequest;
import org.example.requests.savings.TopUpRequest;
import org.example.responses.savings.CreateSavingGoalResponse;
import org.example.responses.savings.GetSavingGoalsResponse;
import org.example.responses.savings.SavingGoal;
import org.example.responses.savings.SavingGoalTransferResponse;

public class SavingApiClient implements SavingServiceClient {

  private final OkHttpClient httpClient;
  private final ObjectMapper mapper;

  public SavingApiClient() {
    this.httpClient = new Builder()
        .readTimeout(2, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build();
    this.mapper = new ObjectMapper();
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public SavingGoal getSavingsByAccountId(String token, String accountUid) throws IOException {
    HttpUrl httpUrl = this.buildSavingGoalsUrl(accountUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.httpClient.newCall(request).execute().body();
    try {
      GetSavingGoalsResponse savingGoals = mapper.readValue(body.string(),
          GetSavingGoalsResponse.class);

      if (savingGoals.savingsGoalList().size() == 0) {
        var createdSavingResult = this.createSaving(token, accountUid);
        var savingGoal = this.getSavingGoal(token, accountUid,
            createdSavingResult.savingsGoalUid());
        return savingGoal;
      }

      return savingGoals.savingsGoalList().stream()
          .min(Comparator.comparingInt(SavingGoal::savedPercentage)).get();
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  @Override
  public SavingGoal getSavingGoal(String token, String accountUid, String savingGoalUid)
      throws IOException {
    HttpUrl httpUrl = this.buildSavingGoalByIdUrl(accountUid, savingGoalUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.httpClient.newCall(request).execute().body();
    try {
      SavingGoal savingGoal = mapper.readValue(body.string(), SavingGoal.class);
      return savingGoal;
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  @Override
  public CreateSavingGoalResponse createSaving(String token, String accountUid) throws IOException {
    var tripToParisGoal = new CreateSavingGoalRequest("Trip to Paris", "GBP",
        new Amount("GBP", 1000));
    var requestBody = mapper.writeValueAsString(tripToParisGoal);
    HttpUrl httpUrl = this.buildSavingGoalsUrl(accountUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .put(RequestBody.create(requestBody, MediaType.parse("application/json")))
        .build();
    ResponseBody body = this.httpClient.newCall(request).execute().body();
    try {
      /**
       * ERROR THROWING HERE
       */
      CreateSavingGoalResponse savingGoalsResult = mapper.readValue(body.string(),
          CreateSavingGoalResponse.class);
      return savingGoalsResult;
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  @Override
  public SavingGoalTransferResponse addMoneyToSaving(String token, String accountUid,
      SavingGoal savingGoal, Integer amountToAdd) throws IOException {
    var topUpRequest = new TopUpRequest(
        new Amount(savingGoal.totalSaved().currency(), amountToAdd));
    var requestBody = this.mapper.writeValueAsString(topUpRequest);
    HttpUrl httpUrl = this.buildTopUpSavingUrl(accountUid, savingGoal.savingsGoalUid());
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .put(RequestBody.create(requestBody, MediaType.parse("application/json")))
        .build();
    ResponseBody body = this.httpClient.newCall(request).execute().body();
    try {
      SavingGoalTransferResponse savingGoalTransferResult = this.mapper.readValue(body.string(),
          SavingGoalTransferResponse.class);
      return savingGoalTransferResult;
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  private HttpUrl buildSavingGoalsUrl(String accountUid) {
    return new HttpUrl
        .Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment(API_PREFIX)
        .addPathSegment(API_V2)
        .addPathSegment(ACCOUNT_RESOURCE_SEGMENT)
        .addPathSegment(accountUid)
        .addPathSegment(SAVINGS_RESOURCE_SEGMENT)
        .build();
  }

  private HttpUrl buildSavingGoalByIdUrl(String accountUid, String savingGoalUid) {
    return new HttpUrl
        .Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment(API_PREFIX)
        .addPathSegment(API_V2)
        .addPathSegment(ACCOUNT_RESOURCE_SEGMENT)
        .addPathSegment(accountUid)
        .addPathSegment(SAVINGS_RESOURCE_SEGMENT)
        .addPathSegment(savingGoalUid)
        .build();
  }

  private HttpUrl buildTopUpSavingUrl(String accountUid, String savingGoalUid) {
    return new HttpUrl
        .Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment(API_PREFIX)
        .addPathSegment(API_V2)
        .addPathSegment(ACCOUNT_RESOURCE_SEGMENT)
        .addPathSegment(accountUid)
        .addPathSegment(SAVINGS_RESOURCE_SEGMENT)
        .addPathSegment(savingGoalUid)
        .addPathSegment(SAVINGS_ADD_MONEY_SEGMENT)
        .addPathSegment(UUID.randomUUID().toString())
        .build();
  }
}
