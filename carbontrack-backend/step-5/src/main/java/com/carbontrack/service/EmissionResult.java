package com.carbontrack.service;

import com.carbontrack.model.EmissionFactor;

import java.math.BigDecimal;

/** What the engine returns: the CO2e value AND the exact factor row that produced it. */
public record EmissionResult(EmissionFactor factor, BigDecimal co2eKg) {}
