package com.rental.houserental.dto.response.transaction;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class TransactionStatsDTO {

    private Double revenue;
    private Long totalDepositTransaction;
    private Long totalUserDeposit;

    public TransactionStatsDTO(Number revenue, Number totalDepositTransaction, Number totalUserDeposit) {
        this.revenue = revenue != null ? revenue.doubleValue() : 0.0;
        this.totalDepositTransaction = totalDepositTransaction != null ? totalDepositTransaction.longValue() : 0;
        this.totalUserDeposit = totalUserDeposit != null ? totalUserDeposit.longValue() : 0;
    }
}
