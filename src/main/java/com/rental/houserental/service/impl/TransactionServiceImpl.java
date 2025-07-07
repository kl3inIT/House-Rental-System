package com.rental.houserental.service.impl;

import com.rental.houserental.dto.response.transaction.TransactionResponseDTO;
import com.rental.houserental.entity.Transaction;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserRole;
import com.rental.houserental.exceptions.user.UserNotFoundException;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.TransactionService;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rental.houserental.constant.ErrorMessageConstant.MSG_400;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;

    @Override
    public TransactionResponseDTO handleTopUpFromSePay(Map<String, String> transactionDetails) {

        Transaction transaction = new Transaction();
        User user = userRepository.findById(extractUserIdFromContent(transactionDetails.get("content")))
                .orElseThrow(() -> new UserNotFoundException("User not found", MSG_400));


        transaction.setAmount(Double.parseDouble(transactionDetails.get("transferAmount")));
        transaction.setSender(transactionDetails.get(user.getEmail()));
        transaction.setReceiver(UserRole.ADMIN.toString());
        transaction.setDescription(transactionDetails.get("content"));
        transaction.setBalanceAfter(user.getBalance());
        transaction.setUser(user);

        return null;
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
}
