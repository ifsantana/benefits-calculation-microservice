package org.example.requests.savings;

import java.io.Serializable;
import org.example.requests.Amount;

public record CreateSavingGoalRequest(String name, String currency, Amount target) implements
    Serializable {
}
