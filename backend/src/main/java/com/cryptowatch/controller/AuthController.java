package com.cryptowatch.controller;

import com.cryptowatch.config.JwtUtil;
import com.cryptowatch.dto.*;
import com.cryptowatch.entity.User;
import com.cryptowatch.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email déjà utilisé"));
        }

        User user = User.builder()
            .id(UUID.randomUUID())
            .email(request.email())
            .name(request.name())
            .password(passwordEncoder.encode(request.password()))
            .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        UserDto userDto = mapToDto(user);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Compte créé", new AuthResponse(token, userDto)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Email ou mot de passe incorrect"));
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        UserDto userDto = mapToDto(user);

        return ResponseEntity.ok(ApiResponse.success(new AuthResponse(token, userDto)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Non authentifié"));
        }

        String userId = (String) authentication.getPrincipal();
        User user = userRepository.findById(UUID.fromString(userId))
            .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Utilisateur introuvable"));
        }

        return ResponseEntity.ok(ApiResponse.success(mapToDto(user)));
    }

    private UserDto mapToDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName(), user.getAvatarUrl());
    }
}
