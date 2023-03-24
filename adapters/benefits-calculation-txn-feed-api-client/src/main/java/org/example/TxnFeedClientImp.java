package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.example.requests.interfaces.HttpClient;
import org.example.responses.txnfeed.FeedItemsResponse;

public class TxnFeedClientImp implements TxnFeedClient, HttpClient {
  private final OkHttpClient httpClient;
  private final ObjectMapper mapper;

  public TxnFeedClientImp() {
    this.httpClient = new Builder()
        .readTimeout(2, TimeUnit.SECONDS)
        .build();
    this.mapper = new ObjectMapper();
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public FeedItemsResponse getTxnFeedItemsByAccountId(String token, String accountUid)
      throws IOException {
    HttpUrl httpUrl = this.create(accountUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.httpClient.newCall(request).execute().body();
    try {
      FeedItemsResponse feedItems = this.mapper.readValue(body.string(),
          FeedItemsResponse.class);

      return new FeedItemsResponse(feedItems.feedItems().stream().filter(feedItem -> feedItem.direction().equals("OUT")).toList());
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  @Override
  public HttpUrl create(String... args) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    return new HttpUrl
        .Builder()
        .scheme("https")
        .host("api-sandbox.starlingbank.com")
        .addPathSegment("api")
        .addPathSegment("v2")
        .addPathSegment("feed")
        .addPathSegment("account")
        .addPathSegment(args[0])
        .addPathSegment("settled-transactions-between")
        .addQueryParameter("minTransactionTimestamp", LocalDateTime.now().minusDays(7L).format(formatter))
        .addQueryParameter("maxTransactionTimestamp", LocalDateTime.now().format(formatter))
        .build();
  }
}
