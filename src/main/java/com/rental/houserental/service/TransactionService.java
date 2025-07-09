package com.rental.houserental.service;

import com.rental.houserental.dto.request.transaction.TransactionRequestDTO;
import com.rental.houserental.dto.response.transaction.TransactionResponseDTO;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface TransactionService {
    void handleTopUpFromSePay(Map<String, String> transactionDetails);

    Long extractUserIdFromContent(String content);

    Page<TransactionResponseDTO> getTransactionHistory(TransactionRequestDTO transactionRequestDTO);
}
