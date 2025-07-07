package com.rental.houserental.entity;

import com.rental.houserental.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Transactions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @Column(name = "Amount", nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false)
    private TransactionType type;

    @Column(name = "Sender", columnDefinition = "NVARCHAR(255)")
    private String sender;

    @Column(name = "Receiver", columnDefinition = "NVARCHAR(255)")
    private String receiver;

    @Column(name = "Description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(name = "BalanceAfter", nullable = false)
    private double balanceAfter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)

    private User user;
}
