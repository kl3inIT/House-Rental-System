package com.rental.houserental.service;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.dto.request.user.ChangePasswordRequestDTO;
import com.rental.houserental.dto.request.user.UpdateProfileRequestDTO;
import com.rental.houserental.dto.response.user.UserProfileResponseDTO;
import com.rental.houserental.entity.User;

public interface UserService {

    User createUser(RegisterRequestDTO request);

    User getCurrentUser();

    User findByEmail(String email);

    void depositBalance(Long id, Double amount);

    UserProfileResponseDTO updateProfile(UpdateProfileRequestDTO request);

    void changePassword(ChangePasswordRequestDTO request);

    UserProfileResponseDTO getCurrentUserProfile();

}
