package org.example.events;

import java.io.Serializable;
import org.example.responses.txnfeed.FeedItem;

public record AccountHolderWebhookDispatchFeedItem(String webhookEventUid, String eventTimestamp, String accountHolderUid, FeedItem content) implements
    Serializable {
}
