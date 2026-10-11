package com.carbontrack.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One row of the "rule table": kg CO2e emitted per 1 unit of an activity type.
 * Stored in the DB so admins can update values without redeploying the app.
 */
@Entity
@Table(name = "emission_factors")
@Getter @Setter @NoArgsConstructor
public class EmissionFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "kg_co2e_per_unit", nullable = false, precision = 12, scale = 6)
    private BigDecimal kgCo2ePerUnit;

    @Column(nullable = false, length = 150)
    private String source;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private boolean active = true;
}
