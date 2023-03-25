package org.example.handlers.interfaces;

import org.example.events.AccountHolderWebhookDispatchFeedItem;
import org.example.handlers.CommandHandler;

public interface RoundUpRealtimeCommandHandler extends CommandHandler<AccountHolderWebhookDispatchFeedItem, Boolean> {
}
