package org.example.responses.savings;

import java.io.Serializable;
import java.util.List;

public record GetSavingGoalsResponse(List<SavingGoal> savingsGoalList) implements
    Serializable {
}
