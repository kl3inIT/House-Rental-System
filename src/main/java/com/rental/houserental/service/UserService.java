package com.rental.houserental.service;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.dto.request.user.ChangePasswordRequestDTO;
import com.rental.houserental.dto.request.user.UpdateProfileRequestDTO;
import com.rental.houserental.dto.response.user.UserProfileResponseDTO;
import com.rental.houserental.entity.User;

import java.util.List;

public interface UserService {

    User createUser(RegisterRequestDTO request);

    User getCurrentUser();

    User findByEmail(String email);

    void depositBalance(Long id, Double amount);

    UserProfileResponseDTO updateProfile(UpdateProfileRequestDTO request);

    void changePassword(ChangePasswordRequestDTO request);

    UserProfileResponseDTO getCurrentUserProfile();

    List<User> getAllLandlords();

    User findById(Long id);
}
