package com.rental.houserental.repository;

import com.rental.houserental.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    @Query("SELECT sc FROM SystemConfig sc WHERE sc.configKey = :key AND sc.isActive = true")
    Optional<SystemConfig> findActiveByKey(@Param("key") String key);

} 