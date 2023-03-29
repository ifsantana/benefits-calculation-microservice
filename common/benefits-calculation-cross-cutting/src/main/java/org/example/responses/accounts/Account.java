package org.example.responses.accounts;

import java.io.Serializable;

public record Account(String accountUid, String accountType, String defaultCategory, String currency, String createdAt, String name) implements
    Serializable {

  public Account(String accountUid, String accountType, String defaultCategory, String currency,
      String createdAt, String name) {
    this.accountUid = accountUid;
    this.accountType = accountType;
    this.defaultCategory = defaultCategory;
    this.currency = currency;
    this.createdAt = createdAt;
    this.name = name;
  }
}