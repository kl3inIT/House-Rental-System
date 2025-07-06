package com.rental.houserental.enums;

import com.rental.houserental.exceptions.transaction.InvalidTransactionTypeException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public enum TransactionType {
    DEPOSIT("Deposit", "User deposits money into the system."),
    WITHDRAW("Withdraw", "User withdraws money from the system."),
    PAYMENT("Payment", "User pays for a rental property."),
    REFUND("Refund", "Refund issued to the user.");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static TransactionType fromString(String type) {
        try {
            return TransactionType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidTransactionTypeException("Invalid transaction type: " + type);
        }
    }

    public boolean isPayment() {
        return this == PAYMENT;
    }

    public boolean isRefund() {
        return this == REFUND;
    }

    public static List<Map<String, String>> getTypeList() {
        return Arrays.stream(TransactionType.values())
                .map(type -> Map.of(
                        "code", type.name(),
                        "displayName", type.getDisplayName(),
                        "description", type.getDescription()
                ))
                .toList();
    }
}
