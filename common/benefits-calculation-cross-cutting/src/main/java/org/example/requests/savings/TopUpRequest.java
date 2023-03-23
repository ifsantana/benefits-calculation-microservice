package org.example.requests.savings;

import java.io.Serializable;
import org.example.requests.Amount;

public record TopUpRequest(Amount amount) implements
    Serializable {
}
