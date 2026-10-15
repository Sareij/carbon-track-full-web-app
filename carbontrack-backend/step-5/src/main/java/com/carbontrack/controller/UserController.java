package com.carbontrack.controller;

import com.carbontrack.dto.ProfileResponse;
import com.carbontrack.dto.UpdateProfileRequest;
import com.carbontrack.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** "me" = whoever the JWT says you are. A user can never read or edit someone else's profile here. */
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me(Authentication authentication) {
        return ResponseEntity.ok(userService.getProfile(authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateMe(Authentication authentication,
                                                    @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(authentication.getName(), request));
    }
}
