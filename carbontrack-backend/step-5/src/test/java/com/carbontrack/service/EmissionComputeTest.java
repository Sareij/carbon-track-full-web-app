package com.carbontrack.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the PURE maths of the engine: EmissionCalculationService.compute(quantity, factor).
 * No database and no Spring needed, so these run in milliseconds.
 *
 * One @ParameterizedTest + @CsvSource = one test method checked against a TABLE of
 * (quantity, factor, expected CO2e) rows. Each row runs as its own test.
 *
 * assertEquals on BigDecimal also compares the SCALE, so "1.9200" (4 decimals) must match exactly.
 */
class EmissionComputeTest {

    private static void check(String quantity, String factor, String expected) {
        BigDecimal actual = EmissionCalculationService.compute(new BigDecimal(quantity), new BigDecimal(factor));
        assertEquals(new BigDecimal(expected), actual,
                () -> quantity + " x " + factor + " should be " + expected);
    }

    // ------------------------------------------------------------------ S5-01
    @ParameterizedTest(name = "transport: {0} km x {1} = {2} kg")
    @CsvSource({
            "10,  0.192, 1.9200",     // CAR_PETROL
            "25,  0.171, 4.2750",     // CAR_DIESEL
            "100, 0.105, 10.5000"     // BUS
    })
    void transport(String quantity, String factor, String expected) {
        check(quantity, factor, expected);
    }

    // ------------------------------------------------------------------ S5-02
    @ParameterizedTest(name = "electricity: {0} kWh x {1} = {2} kg")
    @CsvSource({
            "100, 0.710, 71.0000",    // GRID
            "150, 0.710, 106.5000",   // GRID
            "1,   0.011, 0.0110"      // WIND
    })
    void electricity(String quantity, String factor, String expected) {
        check(quantity, factor, expected);
    }

    // ------------------------------------------------------------------ S5-03
    @ParameterizedTest(name = "food: {0} servings x {1} = {2} kg")
    @CsvSource({
            "2, 6.9, 13.8000",        // BEEF_MEAL
            "3, 0.5, 1.5000",         // VEGAN_MEAL
            "1, 1.8, 1.8000"          // CHICKEN_MEAL
    })
    void food(String quantity, String factor, String expected) {
        check(quantity, factor, expected);
    }

    // ------------------------------------------------------------------ S5-04
    @ParameterizedTest(name = "shopping: {0} USD x {1} = {2} kg")
    @CsvSource({
            "200, 0.5, 100.0000",     // ELECTRONICS
            "50,  0.3, 15.0000",      // CLOTHING
            "10,  0.4, 4.0000"        // GROCERIES
    })
    void shopping(String quantity, String factor, String expected) {
        check(quantity, factor, expected);
    }

    // ------------------------------------------------------------------ S5-13
    /** HALF_UP = ordinary school rounding: a trailing 5 rounds up, anything below 5 rounds down. */
    @ParameterizedTest(name = "rounding: {0} x {1} -> {2}")
    @CsvSource({
            "1, 0.123455, 0.1235",    // ...5 rounds UP
            "1, 0.123449, 0.1234",    // ...49 rounds DOWN
            "3, 0.333333, 1.0000"     // 0.999999 rounds up to 1.0000
    })
    void rounding(String quantity, String factor, String expected) {
        check(quantity, factor, expected);
    }
}
