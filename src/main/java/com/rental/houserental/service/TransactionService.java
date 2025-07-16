package com.rental.houserental.service;

import com.rental.houserental.dto.request.transaction.TransactionRequestDTO;
import com.rental.houserental.dto.response.transaction.TransactionResponseDTO;
import com.rental.houserental.dto.response.transaction.TransactionStatsDTO;
import org.springframework.data.domain.Page;
import java.util.Map;

public interface TransactionService {
    void handleTopUpFromSePay(Map<String, String> transactionDetails);
    Long extractUserIdFromContent(String content);
    Page<TransactionResponseDTO> getTransactionHistory(TransactionRequestDTO transactionRequestDTO);
    Page<TransactionResponseDTO> getTransactionHistoryForAdmin(TransactionRequestDTO transactionRequestDTO);
    TransactionStatsDTO getTransactionStats();
}
