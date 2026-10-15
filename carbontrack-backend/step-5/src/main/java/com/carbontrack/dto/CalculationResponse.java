package com.carbontrack.dto;

import com.carbontrack.model.Category;
import com.carbontrack.service.EmissionResult;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalculationResponse(
        Category category,
        String activityType,
        String unit,
        BigDecimal quantity,
        BigDecimal kgCo2ePerUnit,
        BigDecimal co2eKg,
        Long factorId,
        String factorSource,
        LocalDate factorEffectiveDate
) {
    public static CalculationResponse of(BigDecimal quantity, EmissionResult r) {
        return new CalculationResponse(
                r.factor().getCategory(), r.factor().getActivityType(), r.factor().getUnit(),
                quantity, r.factor().getKgCo2ePerUnit(), r.co2eKg(),
                r.factor().getId(), r.factor().getSource(), r.factor().getEffectiveDate());
    }
}
