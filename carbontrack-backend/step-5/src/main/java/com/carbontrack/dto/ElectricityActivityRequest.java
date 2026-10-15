package com.carbontrack.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Electricity: kWh consumed + energy source (GRID, SOLAR, WIND ...). */
public record ElectricityActivityRequest(
        @NotBlank(message = "Energy source is required")
        @Size(max = 50, message = "Energy source must be at most 50 characters")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Energy source may contain only letters, digits and '_'")
        String energySource,

        @NotNull(message = "kWh is required")
        @Positive(message = "kWh must be greater than 0")
        @DecimalMax(value = "100000", message = "kWh must not exceed 100,000 per entry")
        @Digits(integer = 6, fraction = 3, message = "kWh allows up to 3 decimal places")
        BigDecimal kwh,

        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date
) {}
