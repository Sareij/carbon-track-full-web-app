package com.carbontrack.service;

import com.carbontrack.dto.ProfileResponse;
import com.carbontrack.dto.UpdateProfileRequest;
import com.carbontrack.exception.ResourceNotFoundException;
import com.carbontrack.model.User;
import com.carbontrack.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String username) {
        return ProfileResponse.from(find(username));
    }

    @Transactional
    public ProfileResponse updateProfile(String username, UpdateProfileRequest req) {
        User user = find(username);
        if (req.preferredUnitSystem() != null) {
            user.setPreferredUnitSystem(req.preferredUnitSystem());
        }
        if (req.goalsVisible() != null) {
            user.setGoalsVisible(req.goalsVisible());
        }
        // No explicit save needed: the entity is managed inside this transaction,
        // so Hibernate flushes the change on commit ("dirty checking").
        return ProfileResponse.from(user);
    }

    private User find(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
