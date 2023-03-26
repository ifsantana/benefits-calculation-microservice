package org.example.commands;

public record RoundUpWeeklyCommand(String token, String minTransactionTimestamp, String maxTransactionTimestamp) {
}
