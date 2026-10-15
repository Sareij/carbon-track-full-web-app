package com.carbontrack.dto;

import com.carbontrack.model.UnitSystem;

/** Both fields optional: a null field means "leave unchanged". */
public record UpdateProfileRequest(
        UnitSystem preferredUnitSystem,
        Boolean goalsVisible
) {}
