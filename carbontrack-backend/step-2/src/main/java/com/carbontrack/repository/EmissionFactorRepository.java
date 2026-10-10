package com.carbontrack.repository;

import com.carbontrack.model.EmissionFactor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long> {

    /**
     * Newest ACTIVE factor for this activity type + unit that is already in effect on the given date.
     * "TopBy...OrderByEffectiveDateDesc" = LIMIT 1 with the most recent effective_date first.
     */
    Optional<EmissionFactor> findFirstByActivityTypeAndUnitAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
            String activityType, String unit, LocalDate onDate);

    List<EmissionFactor> findByActiveTrueOrderByCategoryAscActivityTypeAsc();
}
