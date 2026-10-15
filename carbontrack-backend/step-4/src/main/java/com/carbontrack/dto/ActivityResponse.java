package com.carbontrack.dto;

import com.carbontrack.model.ActivityLog;
import com.carbontrack.model.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ActivityResponse(
        Long id,
        Category category,
        String activityType,
        BigDecimal quantity,
        String unit,
        BigDecimal kgCo2ePerUnit,
        BigDecimal co2eKg,
        Long emissionFactorId,
        LocalDate logDate
) {
    public static ActivityResponse from(ActivityLog a) {
        return new ActivityResponse(
                a.getId(), a.getCategory(), a.getActivityType(), a.getQuantity(), a.getUnit(),
                a.getEmissionFactor().getKgCo2ePerUnit(), a.getCo2eKg(),
                a.getEmissionFactor().getId(), a.getLogDate());
    }
}
