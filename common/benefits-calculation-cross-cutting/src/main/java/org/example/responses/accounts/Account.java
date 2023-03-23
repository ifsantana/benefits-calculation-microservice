package org.example.responses.accounts;

import java.io.Serializable;

public record Account(String accountUid, String accountType, String defaultCategory, String currency, String createdAt, String name) implements
    Serializable {
}