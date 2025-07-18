package com.rental.houserental.service;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.dto.request.user.ChangePasswordRequestDTO;
import com.rental.houserental.dto.request.user.UpdateProfileRequestDTO;
import com.rental.houserental.dto.request.landlord.LandlordFilterRequestDTO;
import com.rental.houserental.dto.response.user.UserProfileResponseDTO;
import com.rental.houserental.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    User createUser(RegisterRequestDTO request);

    User getCurrentUser();

    User findByEmail(String email);

    void depositBalance(Long id, Double amount);

    UserProfileResponseDTO updateProfile(UpdateProfileRequestDTO request);

    void changePassword(ChangePasswordRequestDTO request);

    UserProfileResponseDTO getCurrentUserProfile();

    Page<User> getAllLandlords(Pageable pageable);

    Page<User> searchLandlords(LandlordFilterRequestDTO filter, Pageable pageable);

    User findById(Long id);
}
