package com.rental.houserental.repository;

import com.rental.houserental.entity.User;
import com.rental.houserental.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRoleOrderByCreatedAtDesc(UserRole role);
    Page<User> findByRoleOrderByCreatedAtDesc(UserRole role, Pageable pageable);

    @Query(value = "SELECT * FROM Users u WHERE u.Role = 'LANDLORD' " +
            "AND (:name IS NULL OR u.FullName COLLATE Latin1_General_CI_AI LIKE '%' + :name + '%' COLLATE Latin1_General_CI_AI) " +
            "AND (:email IS NULL OR LOWER(u.Email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:phone IS NULL OR u.PhoneNumber LIKE CONCAT('%', :phone, '%')) " +
            "AND (:status IS NULL OR u.Status = :status)",
            nativeQuery = true)
    Page<User> searchLandlords(@Param("name") String name,
                              @Param("email") String email,
                              @Param("phone") String phone,
                              @Param("status") String status,
                              Pageable pageable);
}
