package com.carbontrack.service;

import com.carbontrack.exception.InvalidQuantityException;
import com.carbontrack.exception.UnknownActivityException;
import com.carbontrack.model.Category;
import com.carbontrack.model.EmissionFactor;
import com.carbontrack.repository.EmissionFactorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Locale;

/**
 * THE EMISSION CALCULATION ENGINE.
 *
 *   co2e_kg  =  quantity  x  factor(activityType, unit, date)
 *
 * The factor comes from the emission_factors TABLE (not from code), so an admin can change it
 * without redeploying. This is the "rule engine" pattern: behaviour driven by data.
 */
@Service
public class EmissionCalculationService {

    /** Upper limit that keeps results inside the DB column DECIMAL(14,4). */
    public static final BigDecimal MAX_QUANTITY = new BigDecimal("1000000");

    /** Matches activity_logs.co2e_kg DECIMAL(14,4). */
    public static final int RESULT_SCALE = 4;

    private final EmissionFactorRepository factorRepository;

    public EmissionCalculationService(EmissionFactorRepository factorRepository) {
        this.factorRepository = factorRepository;
    }

    /**
     * @param category  if not null, the factor found must belong to this category
     *                  (stops "FOOD / CAR_PETROL" style mistakes). Pass null for a free lookup.
     * @param onDate    which version of the factor applies; null = today
     */
    @Transactional(readOnly = true)
    public EmissionResult calculate(Category category, String activityType, String unit,
                                    BigDecimal quantity, LocalDate onDate) {

        validateQuantity(quantity);

        String type = normalize(activityType);
        String normalizedUnit = normalize(unit);
        if (type.isEmpty() || normalizedUnit.isEmpty()) {
            throw new UnknownActivityException("Activity type and unit are required");
        }
        LocalDate date = (onDate != null) ? onDate : LocalDate.now();

        EmissionFactor factor = factorRepository
                .findFirstByActivityTypeAndUnitAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                        type, normalizedUnit, date)
                .orElseThrow(() -> new UnknownActivityException(
                        "No active emission factor for activity '" + type + "' with unit '" + normalizedUnit + "'"));

        if (category != null && factor.getCategory() != category) {
            throw new UnknownActivityException(
                    "Activity '" + type + "' belongs to category " + factor.getCategory() + ", not " + category);
        }

        return new EmissionResult(factor, compute(quantity, factor.getKgCo2ePerUnit()));
    }

    /**
     * The pure maths, kept static and free of any database so unit tests can hit it directly.
     * BigDecimal (not double) avoids floating-point errors; HALF_UP is ordinary school rounding.
     */
    public static BigDecimal compute(BigDecimal quantity, BigDecimal kgCo2ePerUnit) {
        return quantity.multiply(kgCo2ePerUnit).setScale(RESULT_SCALE, RoundingMode.HALF_UP);
    }

    // ------------------------------------------------------------------ helpers

    private static void validateQuantity(BigDecimal quantity) {
        if (quantity == null) {
            throw new InvalidQuantityException("Quantity is required");
        }
        if (quantity.signum() < 0) {
            throw new InvalidQuantityException("Quantity must not be negative");
        }
        if (quantity.compareTo(MAX_QUANTITY) > 0) {
            throw new InvalidQuantityException("Quantity must not exceed " + MAX_QUANTITY.toPlainString());
        }
    }

    /** "  car_petrol " -> "CAR_PETROL" so lookups are not case/space sensitive. */
    private static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}
