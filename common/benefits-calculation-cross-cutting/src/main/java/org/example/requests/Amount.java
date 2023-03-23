package org.example.requests;

import java.io.Serializable;

public record Amount(String currency, Integer minorUnits) implements
    Serializable {
}