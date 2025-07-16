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
    RECEIVE_PAYMENT("Receive Payment", "Landlord receives payment from user."),
    PAYMENT("Payment", "User pays for a rental property."),
    REFUND("Refund", "Refund issued to the user."),
    RECEIVE_REFUND("Receive Refund", "User receives a refund for a payment.");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static TransactionType fromString(String type) {
        if(type == null || type.trim().isEmpty()) {
            return null;
        }
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

    public static List<String> getAllTypes() {
        return Arrays.stream(TransactionType.values())
                .map(TransactionType::getDisplayName)
                .toList();
    }
}
