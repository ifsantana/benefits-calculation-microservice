package org.example.requests.savings;

import java.io.Serializable;

public record CreateSavingGoalRequest(String name, String currency, SavingGoalTarget target) implements
    Serializable {
}
