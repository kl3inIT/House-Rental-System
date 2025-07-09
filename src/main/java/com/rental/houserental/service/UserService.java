package com.rental.houserental.service;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.entity.User;

public interface UserService {

    User createUser(RegisterRequestDTO request);
    User getCurrentUser();
    User findByEmail(String email);
    void depositBalance(Long id, Double amount);

}
