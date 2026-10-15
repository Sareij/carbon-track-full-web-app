package com.carbontrack.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Shopping: product category (ELECTRONICS, CLOTHING ...) + amount spent (USD). */
public record ShoppingActivityRequest(
        @NotBlank(message = "Product category is required")
        @Size(max = 50, message = "Product category must be at most 50 characters")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Product category may contain only letters, digits and '_'")
        String productCategory,

        @NotNull(message = "Spend amount is required")
        @Positive(message = "Spend amount must be greater than 0")
        @DecimalMax(value = "1000000", message = "Spend amount must not exceed 1,000,000 per entry")
        @Digits(integer = 7, fraction = 2, message = "Spend amount allows up to 2 decimal places")
        BigDecimal spendAmount,

        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date
) {}
