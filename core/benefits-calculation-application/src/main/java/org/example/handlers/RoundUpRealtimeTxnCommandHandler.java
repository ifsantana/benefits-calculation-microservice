package org.example.handlers;

import java.io.IOException;
import org.example.events.AccountHolderWebhookDispatchFeedItem;
import org.example.handlers.interfaces.RoundUpRealtimeCommandHandler;

public class RoundUpRealtimeTxnCommandHandler implements RoundUpRealtimeCommandHandler {

  @Override
  public Boolean handle(AccountHolderWebhookDispatchFeedItem param) throws IOException {
    return null;
  }
}
