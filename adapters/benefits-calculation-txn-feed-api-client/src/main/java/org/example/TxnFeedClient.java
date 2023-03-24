package org.example;

import java.io.IOException;
import org.example.responses.txnfeed.FeedItemsResponse;

public interface TxnFeedClient {
  FeedItemsResponse getTxnFeedItemsByAccountId(String token, String accountUid) throws IOException;
}
