package com.carbontrack.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Transport: car km, flights, public transit. transportMode = CAR_PETROL, BUS, TRAIN, FLIGHT_SHORT_HAUL ... */
public record TransportActivityRequest(
        @NotBlank(message = "Transport mode is required")
        @Size(max = 50, message = "Transport mode must be at most 50 characters")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Transport mode may contain only letters, digits and '_'")
        String transportMode,

        @NotNull(message = "Distance is required")
        @Positive(message = "Distance must be greater than 0")
        @DecimalMax(value = "20000", message = "Distance must not exceed 20,000 km per entry")
        @Digits(integer = 5, fraction = 3, message = "Distance allows up to 3 decimal places")
        BigDecimal distanceKm,

        /** Optional, defaults to today. */
        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date
) {}
