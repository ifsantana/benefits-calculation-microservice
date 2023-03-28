package org.example;

import static org.example.constants.StarlingApiConstants.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.example.responses.accounts.Account;
import org.example.responses.accounts.AccountsResponse;

public class AccountApiClient implements AccountsServiceClient {

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

      return accountResponse.accounts().get(0);
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  private HttpUrl buildGetAccountUrl() {
    return new HttpUrl
        .Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment(API_PREFIX)
        .addPathSegment(API_V2)
        .addPathSegment(ACCOUNT_COLLECTION_SEGMENT)
        .build();
  }
}
