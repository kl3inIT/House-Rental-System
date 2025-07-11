package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserStatus;
import com.rental.houserental.exceptions.auth.EmailAlreadyExistsException;
import com.rental.houserental.exceptions.auth.PasswordNotMatchException;
import com.rental.houserental.exceptions.user.UserNotFoundException;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.rental.houserental.constant.ErrorMessageConstant.MSG_400;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordNotMatchException("Passwords do not match");
        }
        return userRepository.save(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.PENDING)
                .build());
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return findByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);

    }

    @Override
    public void depositBalance(Long id, Double amount) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found", MSG_400));
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }


}
