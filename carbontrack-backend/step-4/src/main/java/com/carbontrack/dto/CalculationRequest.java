package com.carbontrack.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CalculationRequest(
        @NotBlank(message = "Activity type is required")
        String activityType,

        @NotBlank(message = "Unit is required")
        String unit,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0", message = "Quantity must not be negative")
        @DecimalMax(value = "1000000", message = "Quantity must not exceed 1,000,000")
        @Digits(integer = 7, fraction = 3, message = "Quantity allows up to 3 decimal places")
        BigDecimal quantity,

        /** Optional. Defaults to today. Decides WHICH version of the factor is used. */
        LocalDate date
) {}
