package com.rental.houserental.repository;

import com.rental.houserental.entity.LandlordUpgrade;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.LandlordUpgradeRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandlordUpgradeRepository extends JpaRepository<LandlordUpgrade, Long> {
    Optional<LandlordUpgrade> findByUserAndStatus(User user, LandlordUpgradeRequestStatus status);

    List<LandlordUpgrade> findAllByStatus(LandlordUpgradeRequestStatus status);

    List<LandlordUpgrade> findAllByUser(User user);

}