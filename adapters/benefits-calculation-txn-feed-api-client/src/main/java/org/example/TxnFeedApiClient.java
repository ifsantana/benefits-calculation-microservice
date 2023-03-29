package org.example;

import static org.example.constants.StarlingApiConstants.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.example.responses.txnfeed.FeedItemsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxnFeedApiClient implements TxnFeedServiceClient {
  private static final Logger logger = LoggerFactory.getLogger(TxnFeedApiClient.class);
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
  public FeedItemsResponse getTxnFeedItemsByAccountId(String token, String accountUid, String minTransactionTimestamp, String maxTransactionTimestamp)
      throws IOException {
    HttpUrl httpUrl = this.buildTxnFeedItemsByAccountId(accountUid, minTransactionTimestamp, maxTransactionTimestamp);
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
      logger.error("error retrieving settled transactions for a date range: ", e);
      return null;
    }
  }
}
