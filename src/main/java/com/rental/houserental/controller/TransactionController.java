package com.rental.houserental.controller;

import com.rental.houserental.dto.request.transaction.TransactionRequestDTO;
import com.rental.houserental.dto.response.transaction.TransactionResponseDTO;
import com.rental.houserental.enums.TransactionType;
import com.rental.houserental.service.TransactionService;
import com.rental.houserental.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.rental.houserental.constant.ViewNamesConstant.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${sepay.secret-key}")
    private String sepaySecretKey;

    @GetMapping("/wallet/transactions")
    public String viewTransactions(@Valid @ModelAttribute("transactionRequest") TransactionRequestDTO transactionRequestDTO,
                                   BindingResult bindingResult,
                                   Model model) {
        Page<TransactionResponseDTO> transactions;

        if (bindingResult.hasErrors()) {
            TransactionRequestDTO defaultRequest = new TransactionRequestDTO();
            transactions = transactionService.getTransactionHistory(defaultRequest);
            model.addAttribute("error", bindingResult);
        } else {
            transactions = transactionService.getTransactionHistory(transactionRequestDTO);
        }

        model.addAttribute("transactions", transactions);
        model.addAttribute("types", TransactionType.getAllTypes());
        model.addAttribute("transactionRequest", transactionRequestDTO);
        return WALLET_TRANSACTION;
    }

    @GetMapping(value = "/wallet/deposit")
    public String qrCodeToDepositIntoWallet(Model model) {
        model.addAttribute("content", "DEPOSIT" + userService.getCurrentUser().getId());
        return WALLET_DEPOSIT;
    }

    @PostMapping(value = "/wallet/deposit/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, String> payload,
                                                @RequestHeader("Authorization") String apiKey,
                                                HttpSession session) {
        // Xác thực API Key
        if (!apiKey.equals("Apikey " + sepaySecretKey)) {
            throw new SecurityException("Invalid API Key");
        }

        Long userId = transactionService.extractUserIdFromContent(payload.get("content"));
        double amount = Double.parseDouble(payload.get("transferAmount"));
        userService.depositBalance(userId, amount);
        transactionService.handleTopUpFromSePay(payload);

        // Gửi thông điệp về frontend
        messagingTemplate.convertAndSend("/topic/deposit/" + userId, "success");

        return ResponseEntity.ok("Processed");
    }



}