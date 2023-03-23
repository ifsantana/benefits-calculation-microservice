package org.example.requests.savings;

import java.io.Serializable;

public record SavingGoalTransferResponse(String transferUid, boolean success) implements
    Serializable {
}
