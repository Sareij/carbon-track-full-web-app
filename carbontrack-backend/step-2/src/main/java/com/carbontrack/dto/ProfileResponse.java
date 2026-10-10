package com.carbontrack.dto;

import com.carbontrack.model.AuthProvider;
import com.carbontrack.model.Role;
import com.carbontrack.model.UnitSystem;
import com.carbontrack.model.User;

import java.time.Instant;

/** What we send to the client. Note: the password hash is NEVER included. */
public record ProfileResponse(
        Long id,
        String username,
        String email,
        Role role,
        AuthProvider authProvider,
        UnitSystem preferredUnitSystem,
        boolean goalsVisible,
        Instant createdAt
) {
    public static ProfileResponse from(User u) {
        return new ProfileResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole(),
                u.getAuthProvider(), u.getPreferredUnitSystem(), u.isGoalsVisible(), u.getCreatedAt());
    }
}
