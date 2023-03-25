package org.example;

import static org.example.constants.StarlingApiConstants.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.example.responses.txnfeed.FeedItemsResponse;

public class TxnFeedApiClient implements TxnFeedServiceClient {
  private final OkHttpClient httpClient;
  private final ObjectMapper mapper;

  public TxnFeedApiClient() {
    this.httpClient = new Builder()
        .readTimeout(2, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build();
    this.mapper = new ObjectMapper();
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public FeedItemsResponse getTxnFeedItemsByAccountId(String token, String accountUid)
      throws IOException {
    HttpUrl httpUrl = this.buildTxnFeedItemsByAccountId(accountUid);
    Request request = new Request.Builder()
        .url(httpUrl)
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", token)
        .build();
    ResponseBody body = this.httpClient.newCall(request).execute().body();
    try {
      if(Objects.nonNull(body)) {
        FeedItemsResponse feedItems = this.mapper.readValue(body.string(),
            FeedItemsResponse.class);
        return new FeedItemsResponse(feedItems.feedItems().stream().filter(feedItem -> feedItem.direction().equals("OUT")).toList());
      }
      return null;
    } catch (IOException e) {
      throw new IOException("reason: ", e);
    }
  }

  private HttpUrl buildTxnFeedItemsByAccountId(String accountUid) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    return new HttpUrl
        .Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment(API_PREFIX)
        .addPathSegment(API_V2)
        .addPathSegment(FEED_RESOURCE_SEGMENT)
        .addPathSegment(ACCOUNT_RESOURCE_SEGMENT)
        .addPathSegment(accountUid)
        .addPathSegment(FEED_SETTLED_TXN_BETWEEN_SEGMENT)
        .addQueryParameter("minTransactionTimestamp", LocalDateTime.now().minusDays(7L).format(formatter))
        .addQueryParameter("maxTransactionTimestamp", LocalDateTime.now().format(formatter))
        .build();
  }
}
