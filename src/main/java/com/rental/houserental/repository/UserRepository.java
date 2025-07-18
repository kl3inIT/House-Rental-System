package com.rental.houserental.repository;

import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRoleOrderByCreatedAtDesc(UserRole role);
    Page<User> findByRoleOrderByCreatedAtDesc(UserRole role, Pageable pageable);
}
