package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SavingGoalsApiClient implements SavingGoalsServiceClient {
  private static final Logger logger = LoggerFactory.getLogger(SavingGoalsApiClient.class);
  private final OkHttpClient httpClient;
  private final ObjectMapper mapper;

  public SavingGoalsApiClient() {
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

      if (savingGoals.savingsGoalList().isEmpty()) {
        var createdSavingResult = this.createSavingGoal(token, accountUid);
        return this.getSavingGoal(token, accountUid, createdSavingResult.savingsGoalUid());
      }

      return Optional.of(savingGoals.savingsGoalList().stream()
          .min(Comparator.comparingInt(SavingGoal::savedPercentage))).get().orElse(null);
    } catch (IOException e) {
      logger.error("error retrieving saving goals: ", e);
      return null;
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
      return mapper.readValue(body.string(), SavingGoal.class);
    } catch (IOException e) {
      logger.error("error retrieving saving goal: ", e);
      return null;
    }
  }

  @Override
  public CreateSavingGoalResponse createSavingGoal(String token, String accountUid) throws IOException {
    var tripToParisGoal = new CreateSavingGoalRequest("Dream Trip", "GBP",
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
      return mapper.readValue(body.string(), CreateSavingGoalResponse.class);
    } catch (IOException e) {
      logger.error("error creating a new saving goal: ", e);
      return null;
    }
  }

  @Override
  public SavingGoalTransferResponse addMoneyToSavingGoal(String token, String accountUid,
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
      return this.mapper.readValue(body.string(),
          SavingGoalTransferResponse.class);
    } catch (IOException e) {
      logger.error("error adding money to a saving goal: ", e);
      return null;
    }
  }
}
