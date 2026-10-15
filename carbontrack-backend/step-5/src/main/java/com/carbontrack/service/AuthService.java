package com.carbontrack.service;

import com.carbontrack.dto.AuthResponse;
import com.carbontrack.dto.LoginRequest;
import com.carbontrack.dto.ProfileResponse;
import com.carbontrack.dto.RegisterRequest;
import com.carbontrack.exception.ConflictException;
import com.carbontrack.exception.ResourceNotFoundException;
import com.carbontrack.model.AuthProvider;
import com.carbontrack.model.Role;
import com.carbontrack.model.UnitSystem;
import com.carbontrack.model.User;
import com.carbontrack.repository.UserRepository;
import com.carbontrack.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String username = req.username().trim();
        String email = req.email().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.password()));   // never store the raw password
        user.setRole(Role.USER);                  // self-registration can only create normal users
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setPreferredUnitSystem(UnitSystem.METRIC);
        user.setGoalsVisible(true);
        userRepository.save(user);

        return buildResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        // Throws BadCredentialsException if the username/password pair is wrong.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username().trim(), req.password()));

        User user = userRepository.findByUsername(req.username().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs(), ProfileResponse.from(user));
    }
}
