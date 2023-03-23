package org.example.responses.txnfeed;

import java.io.Serializable;
import java.util.List;

public record FeedItemsResponse(List<FeedItem> feedItems) implements
    Serializable {
}
