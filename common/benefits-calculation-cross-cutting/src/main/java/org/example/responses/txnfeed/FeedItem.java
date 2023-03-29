package org.example.responses.txnfeed;

import java.io.Serializable;
import org.example.responses.Amount;

public record FeedItem(String feedItemUid, String categoryUid, Amount amount, String direction, String spendingCategory, String country) implements
    Serializable {

  public static final class Builder {
    String feedItemUid;
    String categoryUid;
    Amount amount;
    String direction;
    String spendingCategory;
    String country;

    public Builder() {
    }

    public Builder(String feedItemUid, String categoryUid, Amount amount, String direction,
        String spendingCategory, String country) {
      this.feedItemUid = feedItemUid;
      this.categoryUid = categoryUid;
      this.amount = amount;
      this.direction = direction;
      this.spendingCategory = spendingCategory;
      this.country = country;
    }

    public Builder feedItemUid(String feedItemUid) {
      this.feedItemUid = feedItemUid;
      return this;
    }

    public Builder categoryUid(String categoryUid) {
      this.categoryUid = categoryUid;
      return this;
    }

    public Builder amount(Amount amount) {
      this.amount = amount;
      return this;
    }

    public Builder direction(String direction) {
      this.direction = direction;
      return this;
    }

    public Builder spendingCategory(String spendingCategory) {
      this.spendingCategory = spendingCategory;
      return this;
    }

    public Builder country(String country) {
      this.country = country;
      return this;
    }

    public FeedItem build() {
      return new FeedItem(feedItemUid, categoryUid, amount, direction, spendingCategory, country);
    }
  }
}
