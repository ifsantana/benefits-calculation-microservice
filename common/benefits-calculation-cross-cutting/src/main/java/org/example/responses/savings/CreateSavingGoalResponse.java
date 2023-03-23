package org.example.responses.savings;

import java.io.Serializable;

public record CreateSavingGoalResponse(String savingsGoalUid, boolean success) implements
    Serializable {
}
