package com.rental.houserental.controller;

import com.rental.houserental.entity.User;
import com.rental.houserental.service.TransactionService;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rental.houserental.constant.ViewNamesConstant.WALLET_DEPOSIT;
import static com.rental.houserental.constant.ViewNamesConstant.WALLET_TRANSACTION;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;

    @Value("${sepay.secret-key}")
    private String sepaySecretKey;

    @GetMapping(value = "/wallet/transactions")
    public String viewTransactions() {
        return WALLET_TRANSACTION;
    }

    @GetMapping(value = "/wallet/deposit")
    public String qrCodeToDepositIntoWallet(Model model) {
        model.addAttribute("content", "DEPOSIT" + userService.getCurrentUser().getId());
        return WALLET_DEPOSIT;
    }

    @PostMapping(value = "/wallet/deposit/webhook")
    public String handleWebhookFromSePay(@RequestBody Map<String, Object> payload,
                                         @RequestHeader("Authorization") String apiKey ) {

        // Validate API Key
        if (!apiKey.equals("Apikey " + sepaySecretKey)) {
            throw new SecurityException("Invalid API Key");
        }


        Long userId = transactionService.extractUserIdFromContent(payload.get("content").toString());
        double amount = Double.parseDouble(payload.get("transferAmount").toString());
        userService.depositBalance(userId, amount);



        return WALLET_DEPOSIT;
    }

}
