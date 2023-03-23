package org.example.responses.accounts;

import java.io.Serializable;
import java.util.List;

public record AccountsResponse(List<Account> accounts) implements
    Serializable {
}