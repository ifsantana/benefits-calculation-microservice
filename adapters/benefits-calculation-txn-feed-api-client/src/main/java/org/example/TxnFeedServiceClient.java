package org.example;

import java.io.IOException;
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
}
