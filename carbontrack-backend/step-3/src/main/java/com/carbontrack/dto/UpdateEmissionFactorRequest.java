package com.carbontrack.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Admin: correct a factor. Every field is optional; a null field means "leave unchanged". */
public record UpdateEmissionFactorRequest(
        @DecimalMin(value = "0", message = "kgCo2ePerUnit must not be negative")
        @DecimalMax(value = "100000", message = "kgCo2ePerUnit is unrealistically large")
        @Digits(integer = 6, fraction = 6, message = "kgCo2ePerUnit allows up to 6 decimal places")
        BigDecimal kgCo2ePerUnit,

        @Size(max = 150, message = "Source must be at most 150 characters")
        @Pattern(regexp = "(?s).*\\S.*", message = "Source must not be blank")
        String source,

        Boolean active
) {}
