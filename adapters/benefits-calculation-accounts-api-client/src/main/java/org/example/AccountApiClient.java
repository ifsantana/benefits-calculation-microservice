package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.example.responses.accounts.Account;
import org.example.responses.accounts.AccountsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountApiClient implements AccountsServiceClient {
  private static final Logger logger = LoggerFactory.getLogger(AccountApiClient.class);
  private final OkHttpClient httpClient;
  private final ObjectMapper mapper;

  public AccountApiClient() {
    this.httpClient = new Builder()
        .readTimeout(2, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build();
    this.mapper = new ObjectMapper();
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public Account getAccount(String token) throws IOException {
    Request request = new Request.Builder()
        .url(buildGetAccountUrl())
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.httpClient.newCall(request).execute().body();
    try {
      AccountsResponse accountResponse = mapper.readValue(body.string(),
          AccountsResponse.class);

      return Optional.of(accountResponse.accounts().get(0)).orElse(null);
    } catch (IOException e) {
      logger.error("Error retrieving account from accounts api: ", e);
      return null;
    }
  }
}
