package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.auth.RegisterRequestDTO;
import com.rental.houserental.dto.request.user.ChangePasswordRequestDTO;
import com.rental.houserental.dto.request.user.UpdateProfileRequestDTO;
import com.rental.houserental.dto.response.user.UserProfileResponseDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserStatus;
import com.rental.houserental.exceptions.auth.EmailAlreadyExistsException;
import com.rental.houserental.exceptions.auth.InvalidCredentialsException;
import com.rental.houserental.exceptions.auth.PasswordNotMatchException;
import com.rental.houserental.exceptions.user.UserNotFoundException;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static com.rental.houserental.constant.ErrorMessageConstant.MSG_400;

import java.util.List;
import com.rental.houserental.enums.UserRole;
import com.rental.houserental.dto.request.landlord.LandlordFilterRequestDTO;

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
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void depositBalance(Long id, Double amount) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found", MSG_400));
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }

    @Override
    public UserProfileResponseDTO updateProfile(UpdateProfileRequestDTO request) {
        User currentUser = getCurrentUser();
        currentUser.setName(request.getName());
        currentUser.setPhone(request.getPhone());
        currentUser.setAddress(request.getAddress());
        // Convert LocalDate (DTO) -> LocalDateTime (entity)
        currentUser.setDateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth().atStartOfDay() : null);
        currentUser.setGender(request.getGender());

        User updatedUser = userRepository.save(currentUser);
        return mapToUserProfileResponseDTO(updatedUser);
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO request) {
        User currentUser = getCurrentUser();

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Check if new password matches confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordNotMatchException("New password and confirm password do not match");
        }

        // Update password
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    @Override
    public UserProfileResponseDTO getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        UpdateProfileRequestDTO updateProfileRequest = new UpdateProfileRequestDTO();
        updateProfileRequest.setName(currentUser.getName());
        updateProfileRequest.setEmail(currentUser.getEmail());
        updateProfileRequest.setPhone(currentUser.getPhone());
        updateProfileRequest.setAddress(currentUser.getAddress());
        updateProfileRequest.setGender(currentUser.getGender());
        if (currentUser.getDateOfBirth() != null) {
            updateProfileRequest.setDateOfBirth(currentUser.getDateOfBirth().toLocalDate());
        }
        return mapToUserProfileResponseDTO(currentUser);
    }

    @Override
    public Page<User> getAllLandlords(Pageable pageable) {
        return userRepository.findByRoleOrderByCreatedAtDesc(UserRole.LANDLORD, pageable);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public Page<User> searchLandlords(LandlordFilterRequestDTO filter, Pageable pageable) {
        // Xác định sort
        String sortBy = filter.getSortBy() != null ? filter.getSortBy() : "createdAt";
        String sortDir = filter.getSortDir() != null ? filter.getSortDir() : "desc";
        Sort sort;
        switch (sortBy) {
            case "status":
                sort = Sort.by(Sort.Direction.fromString(sortDir), "status"); break;
            case "balance":
                sort = Sort.by(Sort.Direction.fromString(sortDir), "balance"); break;
            case "createdAt":
            default:
                sort = Sort.by(Sort.Direction.fromString(sortDir), "createdAt"); break;
        }
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        com.rental.houserental.enums.UserStatus status = null;
        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            String statusInput = filter.getStatus().trim();
            for (com.rental.houserental.enums.UserStatus us : com.rental.houserental.enums.UserStatus.values()) {
                if (us.getDisplayName().equalsIgnoreCase(statusInput) || us.name().equalsIgnoreCase(statusInput)) {
                    status = us;
                    break;
                }
            }
        }
        return userRepository.searchLandlords(
            isBlank(filter.getName()) ? null : filter.getName(),
            isBlank(filter.getEmail()) ? null : filter.getEmail(),
            isBlank(filter.getPhone()) ? null : filter.getPhone(),
            status != null ? status.name() : null,
            sortedPageable
        );
    }

    private UserProfileResponseDTO mapToUserProfileResponseDTO(User user) {
        return UserProfileResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .balance(user.getBalance())
                .gender(user.getGender())
                .status(user.getStatus())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
