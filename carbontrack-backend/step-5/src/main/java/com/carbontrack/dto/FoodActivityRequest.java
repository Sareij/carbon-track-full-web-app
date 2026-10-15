package com.carbontrack.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Food: meal type (BEEF_MEAL, VEGAN_MEAL ...) + number of servings. */
public record FoodActivityRequest(
        @NotBlank(message = "Meal type is required")
        @Size(max = 50, message = "Meal type must be at most 50 characters")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Meal type may contain only letters, digits and '_'")
        String mealType,

        @NotNull(message = "Servings is required")
        @Positive(message = "Servings must be greater than 0")
        @DecimalMax(value = "100", message = "Servings must not exceed 100 per entry")
        @Digits(integer = 3, fraction = 2, message = "Servings allows up to 2 decimal places")
        BigDecimal servings,

        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date
) {}
