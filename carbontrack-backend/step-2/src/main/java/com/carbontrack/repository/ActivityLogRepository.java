package com.carbontrack.repository;

import com.carbontrack.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserIdAndLogDateBetweenOrderByLogDateDesc(Long userId, LocalDate from, LocalDate to);
}
