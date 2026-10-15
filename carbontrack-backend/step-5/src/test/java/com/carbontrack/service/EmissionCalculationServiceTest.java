package com.carbontrack.service;

import com.carbontrack.exception.InvalidQuantityException;
import com.carbontrack.exception.UnknownActivityException;
import com.carbontrack.model.Category;
import com.carbontrack.model.EmissionFactor;
import com.carbontrack.repository.EmissionFactorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests the engine's behaviour (lookup, boundaries, errors) WITHOUT a real database.
 * The repository is a Mockito "mock": a stand-in whose answers we script in each test.
 * Mockito strict mode also fails a test if a scripted answer is never used.
 */
@ExtendWith(MockitoExtension.class)
class EmissionCalculationServiceTest {

    @Mock
    private EmissionFactorRepository repository;

    private EmissionCalculationService service;

    @BeforeEach
    void setUp() {
        service = new EmissionCalculationService(repository);
    }

    // ------------------------------------------------------------------ helpers

    private static EmissionFactor factor(long id, Category category, String type, String unit, String perUnit) {
        EmissionFactor f = new EmissionFactor();
        f.setId(id);
        f.setCategory(category);
        f.setActivityType(type);
        f.setUnit(unit);
        f.setKgCo2ePerUnit(new BigDecimal(perUnit));
        f.setSource("Test data");
        f.setEffectiveDate(LocalDate.of(2024, 1, 1));
        f.setActive(true);
        return f;
    }

    /** Script the repository: "when asked for THIS type + unit (any date), return THIS factor". */
    private void givenFactor(EmissionFactor f) {
        when(repository.findFirstByActivityTypeAndUnitAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                eq(f.getActivityType()), eq(f.getUnit()), any(LocalDate.class)))
                .thenReturn(Optional.of(f));
    }

    private void givenNoFactor(String type, String unit) {
        when(repository.findFirstByActivityTypeAndUnitAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                eq(type), eq(unit), any(LocalDate.class)))
                .thenReturn(Optional.empty());
    }

    // ------------------------------------------------------------------ S5-05
    @Test
    void zeroQuantity() {
        givenFactor(factor(1, Category.TRANSPORT, "CAR_PETROL", "KM", "0.192"));

        EmissionResult result = service.calculate(Category.TRANSPORT, "CAR_PETROL", "KM", BigDecimal.ZERO, null);

        assertEquals(new BigDecimal("0.0000"), result.co2eKg());
    }

    // ------------------------------------------------------------------ S5-06
    @Test
    void maxQuantity() {
        givenFactor(factor(11, Category.FOOD, "BEEF_MEAL", "SERVING", "6.9"));

        EmissionResult result = service.calculate(Category.FOOD, "BEEF_MEAL", "SERVING",
                EmissionCalculationService.MAX_QUANTITY, null);

        assertEquals(new BigDecimal("6900000.0000"), result.co2eKg());
        // activity_logs.co2e_kg is DECIMAL(14,4): at most 14 digits in total.
        assertTrue(result.co2eKg().precision() <= 14, "result must fit DECIMAL(14,4)");
    }

    // ------------------------------------------------------------------ S5-07
    @Test
    void aboveMax() {
        BigDecimal tooMuch = EmissionCalculationService.MAX_QUANTITY.add(BigDecimal.ONE);   // 1,000,001

        assertThrows(InvalidQuantityException.class,
                () -> service.calculate(Category.TRANSPORT, "CAR_PETROL", "KM", tooMuch, null));
        verifyNoInteractions(repository);        // rejected BEFORE any database lookup
    }

    // ------------------------------------------------------------------ S5-08
    @Test
    void negative() {
        assertThrows(InvalidQuantityException.class,
                () -> service.calculate(Category.TRANSPORT, "CAR_PETROL", "KM", new BigDecimal("-1"), null));
        verifyNoInteractions(repository);
    }

    // ------------------------------------------------------------------ S5-09
    @Test
    void nullQuantity() {
        assertThrows(InvalidQuantityException.class,
                () -> service.calculate(Category.TRANSPORT, "CAR_PETROL", "KM", null, null));
        verifyNoInteractions(repository);
    }

    // ------------------------------------------------------------------ S5-10
    @Test
    void unknownActivity() {
        givenNoFactor("ROCKET", "KM");

        UnknownActivityException ex = assertThrows(UnknownActivityException.class,
                () -> service.calculate(Category.TRANSPORT, "ROCKET", "KM", BigDecimal.TEN, null));

        assertTrue(ex.getMessage().contains("ROCKET"), "message should name the unknown activity");
    }

    // ------------------------------------------------------------------ S5-11
    @Test
    void categoryMismatch() {
        // CAR_PETROL exists, but it is a TRANSPORT factor: asking for it as FOOD must fail.
        givenFactor(factor(1, Category.TRANSPORT, "CAR_PETROL", "KM", "0.192"));

        UnknownActivityException ex = assertThrows(UnknownActivityException.class,
                () -> service.calculate(Category.FOOD, "CAR_PETROL", "KM", BigDecimal.TEN, null));

        assertTrue(ex.getMessage().contains("TRANSPORT") && ex.getMessage().contains("FOOD"),
                "message should name both categories");
    }

    // ------------------------------------------------------------------ S5-12
    @Test
    void normalisation() {
        givenFactor(factor(1, Category.TRANSPORT, "CAR_PETROL", "KM", "0.192"));

        EmissionResult result = service.calculate(Category.TRANSPORT, " car_petrol ", "km", BigDecimal.TEN, null);

        assertEquals(new BigDecimal("1.9200"), result.co2eKg());
        // The repository must have been queried with the CLEANED values.
        verify(repository).findFirstByActivityTypeAndUnitAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                eq("CAR_PETROL"), eq("KM"), any(LocalDate.class));
    }
}
