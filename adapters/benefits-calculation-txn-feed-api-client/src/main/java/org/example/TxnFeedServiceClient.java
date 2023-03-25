package org.example;

import java.io.IOException;
import org.example.responses.txnfeed.FeedItemsResponse;

public interface TxnFeedServiceClient {

  /**
   *
   * @param - token
   * @param - accountUid
   * @return {@link FeedItemsResponse}
   * @throws IOException
   */
  FeedItemsResponse getTxnFeedItemsByAccountId(String token, String accountUid) throws IOException;
}
