package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.transaction.TransactionRequestDTO;
import com.rental.houserental.dto.response.transaction.TransactionResponseDTO;
import com.rental.houserental.dto.response.transaction.TransactionStatsDTO;
import com.rental.houserental.entity.Transaction;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.TransactionType;
import com.rental.houserental.exceptions.user.UserNotFoundException;
import com.rental.houserental.repository.TransactionRepository;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rental.houserental.constant.ErrorMessageConstant.MSG_400;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void handleTopUpFromSePay(Map<String, String> transactionDetails) {

        Transaction transaction = new Transaction();
        User user = userRepository.findById(extractUserIdFromContent(transactionDetails.get("content")))
                .orElseThrow(() -> new UserNotFoundException("User not found", MSG_400));


        transaction.setAmount(Double.parseDouble(transactionDetails.get("transferAmount")));
        transaction.setDescription("Top-up from SePay");
        transaction.setBalanceAfter(user.getBalance());
        transaction.setUser(user);
        transaction.setType(TransactionType.DEPOSIT);
        transactionRepository.save(transaction);
    }

    @Override
    public Long extractUserIdFromContent(String content) {
        String regex = "DEPOSIT(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        String mainContent = null;
        if (matcher.find()) {
            mainContent = matcher.group();
        } else {
            throw new IllegalArgumentException("Not Found transaction code");
        }

        mainContent = mainContent.replace("DEPOSIT", "");
        return Long.parseLong(mainContent);
    }

    @Override
    public Page<TransactionResponseDTO> getTransactionHistory(TransactionRequestDTO transactionRequestDTO) {
        LocalDateTime dateFrom = transactionRequestDTO.getDateFrom() != null
                ? transactionRequestDTO.getDateFrom().atStartOfDay()
                : null;

        LocalDateTime dateTo = transactionRequestDTO.getDateTo() != null
                ? transactionRequestDTO.getDateTo().atTime(23, 59, 59)
                : null;

        Pageable pageable = PageRequest.of(transactionRequestDTO.getPage(), transactionRequestDTO.getSize());
        Page<Transaction> transactions = transactionRepository.findAllBySearchRequest(
                TransactionType.fromString(transactionRequestDTO.getType()),
                dateFrom,
                dateTo,
                transactionRequestDTO.getAmountFrom(),
                transactionRequestDTO.getAmountTo(),
                getCurrentUser().getId(),
                pageable
        );

        return transactions.map(this::transactionResponseDTO);
    }

    @Override
    public Page<TransactionResponseDTO> getTransactionHistoryForAdmin(TransactionRequestDTO transactionRequestDTO) {
        LocalDateTime dateFrom = transactionRequestDTO.getDateFrom() != null
                ? transactionRequestDTO.getDateFrom().atStartOfDay()
                : null;

        LocalDateTime dateTo = transactionRequestDTO.getDateTo() != null
                ? transactionRequestDTO.getDateTo().atTime(23, 59, 59)
                : null;

        Pageable pageable = PageRequest.of(transactionRequestDTO.getPage(), transactionRequestDTO.getSize());
        Page<Transaction> transactions = transactionRepository.findAllBySearchRequestForAdmin(
                TransactionType.fromString(transactionRequestDTO.getType()),
                dateFrom,
                dateTo,
                transactionRequestDTO.getAmountFrom(),
                transactionRequestDTO.getAmountTo(),
                transactionRequestDTO.getUserEmail(),
                pageable
        );
        return transactions.map(this::transactionResponseDTO);
    }

    @Override
    public TransactionStatsDTO getTransactionStats() {
        return transactionRepository.getDepositStats();
    }




    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found", MSG_400));
    }




    private TransactionResponseDTO transactionResponseDTO(Transaction transaction) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        return TransactionResponseDTO.builder()
                .amount(transaction.getAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .description(transaction.getDescription())
                .type(transaction.getType().toString())
                .date(transaction.getCreatedAt().format(formatter))
                .userEmail(transaction.getUser().getEmail())
                .build();
    }
}
