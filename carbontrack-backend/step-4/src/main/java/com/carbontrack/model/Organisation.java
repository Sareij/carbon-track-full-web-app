package com.carbontrack.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "organisations")
@Getter @Setter @NoArgsConstructor
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    // Plain id (not a @ManyToOne) to avoid a circular User <-> Organisation mapping.
    @Column(name = "admin_user_id")
    private Long adminUserId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;
}
