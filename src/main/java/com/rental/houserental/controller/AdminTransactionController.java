package com.rental.houserental.controller;

import com.rental.houserental.dto.request.transaction.TransactionRequestDTO;
import com.rental.houserental.dto.response.transaction.TransactionResponseDTO;
import com.rental.houserental.enums.TransactionType;
import com.rental.houserental.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.rental.houserental.constant.ViewNamesConstant.ADMIN_TRANSACTION;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping("/transactions")
    public String viewTransactions(@Valid @ModelAttribute("transactionRequest") TransactionRequestDTO transactionRequestDTO,
                                   BindingResult bindingResult,
                                   Model model,
                                   HttpServletRequest request) {
        if (transactionRequestDTO.getType() == null) {
            transactionRequestDTO.setType(TransactionType.DEPOSIT.getDisplayName());
        }

        Page<TransactionResponseDTO> transactions;

        if (bindingResult.hasErrors()) {
            TransactionRequestDTO defaultRequest = new TransactionRequestDTO();
            transactions = transactionService.getTransactionHistoryForAdmin(defaultRequest);
            model.addAttribute("error", bindingResult);
        } else {
            transactions = transactionService.getTransactionHistoryForAdmin(transactionRequestDTO);
        }


        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("transactions", transactions);
        model.addAttribute("types", TransactionType.getAllTypes());
        model.addAttribute("transactionRequest", transactionRequestDTO);
        model.addAttribute("stats", transactionService.getTransactionStats());
        return ADMIN_TRANSACTION;
    }


}
