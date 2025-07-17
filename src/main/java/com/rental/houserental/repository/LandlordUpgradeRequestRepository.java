package com.rental.houserental.repository;

import com.rental.houserental.entity.LandlordUpgradeRequest;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.LandlordUpgradeRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandlordUpgradeRequestRepository extends JpaRepository<LandlordUpgradeRequest, Long> {
    Optional<LandlordUpgradeRequest> findByUserAndStatus(User user, LandlordUpgradeRequestStatus status);

    List<LandlordUpgradeRequest> findAllByStatus(LandlordUpgradeRequestStatus status);

    List<LandlordUpgradeRequest> findAllByUser(User user);

}