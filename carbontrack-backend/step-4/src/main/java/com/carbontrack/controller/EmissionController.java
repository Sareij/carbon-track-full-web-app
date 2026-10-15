package com.carbontrack.controller;

import com.carbontrack.dto.CalculationRequest;
import com.carbontrack.dto.CalculationResponse;
import com.carbontrack.dto.EmissionFactorResponse;
import com.carbontrack.model.Category;
import com.carbontrack.service.EmissionCalculationService;
import com.carbontrack.service.EmissionFactorService;
import com.carbontrack.service.EmissionResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Any logged-in user: see the factors and preview a calculation (nothing is saved). */
@RestController
@RequestMapping("/api")
public class EmissionController {

    private final EmissionFactorService factorService;
    private final EmissionCalculationService calculationService;

    public EmissionController(EmissionFactorService factorService, EmissionCalculationService calculationService) {
        this.factorService = factorService;
        this.calculationService = calculationService;
    }

    @GetMapping("/emission-factors")
    public ResponseEntity<List<EmissionFactorResponse>> factors(@RequestParam(required = false) Category category) {
        return ResponseEntity.ok(factorService.listCurrent(category));
    }

    @PostMapping("/emissions/calculate")
    public ResponseEntity<CalculationResponse> calculate(@Valid @RequestBody CalculationRequest req) {
        EmissionResult result = calculationService.calculate(null, req.activityType(), req.unit(), req.quantity(), req.date());
        return ResponseEntity.ok(CalculationResponse.of(req.quantity(), result));
    }
}
