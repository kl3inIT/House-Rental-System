package com.rental.houserental.service;

import com.rental.houserental.dto.response.transaction.TransactionResponseDTO;


import java.util.Map;

public interface TransactionService {
    TransactionResponseDTO handleTopUpFromSePay(Map<String, String> transactionDetails);
    Long extractUserIdFromContent(String content);
}
