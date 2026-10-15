package com.carbontrack.controller;

import com.carbontrack.dto.CreateEmissionFactorRequest;
import com.carbontrack.dto.EmissionFactorResponse;
import com.carbontrack.dto.UpdateEmissionFactorRequest;
import com.carbontrack.service.EmissionFactorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ADMIN only. Two layers protect it:
 *  1) SecurityConfig:  /api/admin/**  ->  hasRole("ADMIN")
 *  2) @PreAuthorize here (defence in depth, in case someone later changes the URL rules)
 */
@RestController
@RequestMapping("/api/admin/emission-factors")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmissionFactorController {

    private final EmissionFactorService service;

    public AdminEmissionFactorController(EmissionFactorService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EmissionFactorResponse>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping
    public ResponseEntity<EmissionFactorResponse> create(@Valid @RequestBody CreateEmissionFactorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmissionFactorResponse> update(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateEmissionFactorRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}
