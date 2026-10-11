package com.carbontrack.dto;

import com.carbontrack.model.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Admin: add a new factor row (use a later effectiveDate to "revise" an existing factor). */
public record CreateEmissionFactorRequest(
        @NotNull(message = "Category is required")
        Category category,

        @NotBlank(message = "Activity type is required")
        @Size(max = 50, message = "Activity type must be at most 50 characters")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Activity type may contain only letters, digits and '_'")
        String activityType,

        @NotBlank(message = "Unit is required")
        @Size(max = 20, message = "Unit must be at most 20 characters")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "Unit may contain only letters, digits and '_'")
        String unit,

        @NotNull(message = "kgCo2ePerUnit is required")
        @DecimalMin(value = "0", message = "kgCo2ePerUnit must not be negative")
        @DecimalMax(value = "100000", message = "kgCo2ePerUnit is unrealistically large")
        @Digits(integer = 6, fraction = 6, message = "kgCo2ePerUnit allows up to 6 decimal places")
        BigDecimal kgCo2ePerUnit,

        @NotBlank(message = "Source is required (e.g. 'EPA 2024')")
        @Size(max = 150, message = "Source must be at most 150 characters")
        String source,

        @NotNull(message = "effectiveDate is required")
        LocalDate effectiveDate
) {}
