package com.carbontrack.controller;

import com.carbontrack.dto.*;
import com.carbontrack.model.Category;
import com.carbontrack.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Every endpoint works ONLY on the caller's own logs: the username comes from the JWT, never from the request. */
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/transport")
    public ResponseEntity<ActivityResponse> logTransport(Authentication auth,
                                                         @Valid @RequestBody TransportActivityRequest req) {
        return created(activityService.log(auth.getName(), Category.TRANSPORT,
                req.transportMode(), req.distanceKm(), req.date()));
    }

    @PostMapping("/electricity")
    public ResponseEntity<ActivityResponse> logElectricity(Authentication auth,
                                                           @Valid @RequestBody ElectricityActivityRequest req) {
        return created(activityService.log(auth.getName(), Category.ELECTRICITY,
                req.energySource(), req.kwh(), req.date()));
    }

    @PostMapping("/food")
    public ResponseEntity<ActivityResponse> logFood(Authentication auth,
                                                    @Valid @RequestBody FoodActivityRequest req) {
        return created(activityService.log(auth.getName(), Category.FOOD,
                req.mealType(), req.servings(), req.date()));
    }

    @PostMapping("/shopping")
    public ResponseEntity<ActivityResponse> logShopping(Authentication auth,
                                                        @Valid @RequestBody ShoppingActivityRequest req) {
        return created(activityService.log(auth.getName(), Category.SHOPPING,
                req.productCategory(), req.spendAmount(), req.date()));
    }

    /** GET /api/activities?from=2026-10-01&to=2026-10-31&category=FOOD  (all params optional; default = last 30 days) */
    @GetMapping
    public ResponseEntity<List<ActivityResponse>> list(
            Authentication auth,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Category category) {
        return ResponseEntity.ok(activityService.list(auth.getName(), from, to, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponse> getOne(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(activityService.getOne(auth.getName(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        activityService.delete(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ActivityResponse> created(ActivityResponse body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
