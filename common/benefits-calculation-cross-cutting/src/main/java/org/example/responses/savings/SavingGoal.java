package org.example.responses.savings;

import java.io.Serializable;
import org.example.responses.Amount;

public record SavingGoal(String savingsGoalUid, String name, Amount target, Amount totalSaved, Integer savedPercentage) implements
    Serializable {
}

