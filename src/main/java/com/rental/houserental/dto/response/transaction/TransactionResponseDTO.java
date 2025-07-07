package com.rental.houserental.dto.response.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionResponseDTO {
    private String type;
    private double amount;
    private String description;
    private String sender;
    private String receiver;
    private String date;
    private String balanceAfter;
}
