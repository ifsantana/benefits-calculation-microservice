package org.example.usecases.roundup.interfaces;

import java.util.List;
import org.example.events.AccountHolderWebhookDispatchFeedItem;
import org.example.responses.txnfeed.FeedItem;

public interface RoundUpRealtimeUseCase {
  void execute(AccountHolderWebhookDispatchFeedItem feedItem);

  /**
   *
   * @param - list
   * @return {@link Integer} that represents the rounded up amount in minor units.
   */
  Integer calculateRoundUp(FeedItem feedTxn);
}
