package com.rental.houserental.dto.response.transaction;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private String type;
    private double amount;
    private String description;
    private String sender;
    private String receiver;
    private String date;
    private double balanceAfter;
}
