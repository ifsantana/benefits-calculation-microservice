package org.example.usecases.roundup.interfaces;

import org.example.events.AccountHolderWebhookDispatchFeedItem;

public interface RoundUpRealtimeUseCase {
  void execute(AccountHolderWebhookDispatchFeedItem feedItem);
}
