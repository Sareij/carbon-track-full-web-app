package com.carbontrack.security;

import com.carbontrack.model.User;
import com.carbontrack.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/** Teaches Spring Security how to load OUR users from the users table. */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Google users have no password: an empty value can never match a BCrypt hash.
        String password = user.getPasswordHash() == null ? "" : user.getPasswordHash();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                password,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
