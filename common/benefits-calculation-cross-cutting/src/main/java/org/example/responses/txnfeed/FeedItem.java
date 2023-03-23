package org.example.responses.txnfeed;

import java.io.Serializable;
import org.example.responses.Amount;

public record FeedItem(String feedItemUid, String categoryUid, Amount amount, String direction, String spendingCategory, String country) implements
    Serializable {
}
