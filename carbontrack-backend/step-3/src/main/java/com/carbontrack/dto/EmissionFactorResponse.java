package com.carbontrack.dto;

import com.carbontrack.model.Category;
import com.carbontrack.model.EmissionFactor;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmissionFactorResponse(
        Long id,
        Category category,
        String activityType,
        String unit,
        BigDecimal kgCo2ePerUnit,
        String source,
        LocalDate effectiveDate,
        boolean active
) {
    public static EmissionFactorResponse from(EmissionFactor f) {
        return new EmissionFactorResponse(f.getId(), f.getCategory(), f.getActivityType(), f.getUnit(),
                f.getKgCo2ePerUnit(), f.getSource(), f.getEffectiveDate(), f.isActive());
    }
}
