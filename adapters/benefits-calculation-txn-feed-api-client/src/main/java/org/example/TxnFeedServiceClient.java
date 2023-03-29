package org.example;

import static org.example.constants.StarlingApiConstants.ACCOUNT_RESOURCE_SEGMENT;
import static org.example.constants.StarlingApiConstants.API_PREFIX;
import static org.example.constants.StarlingApiConstants.API_V2;
import static org.example.constants.StarlingApiConstants.FEED_RESOURCE_SEGMENT;
import static org.example.constants.StarlingApiConstants.FEED_SETTLED_TXN_BETWEEN_SEGMENT;
import static org.example.constants.StarlingApiConstants.HOST;
import static org.example.constants.StarlingApiConstants.SCHEME;

import java.io.IOException;
import okhttp3.HttpUrl;
import org.example.responses.txnfeed.FeedItemsResponse;

public interface TxnFeedServiceClient {

  /**
   * This method consumes the transaction feeds api and returns all settled transactions in a date
   * range for an specific account.
   *
   * @param - token
   * @param - accountUid
   * @return {@link FeedItemsResponse}
   * @throws IOException
   */
  FeedItemsResponse getTxnFeedItemsByAccountId(String token, String accountUid,
      String minTransactionTimestamp, String maxTransactionTimestamp) throws IOException;

  default HttpUrl buildTxnFeedItemsByAccountId(String accountUid, String minTransactionTimestamp, String maxTransactionTimestamp) {
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
        .addQueryParameter("minTransactionTimestamp", minTransactionTimestamp)
        .addQueryParameter("maxTransactionTimestamp", maxTransactionTimestamp)
        .build();
  }
}
