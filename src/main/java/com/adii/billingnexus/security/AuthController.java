package com.adii.billingnexus.security;

import com.adii.billingnexus.exception.InvalidCredentialsException;
import com.adii.billingnexus.dto.request.LoginRequest;
import com.adii.billingnexus.dto.request.RegisterRequest;
import com.adii.billingnexus.dto.response.AuthResponse;
import com.adii.billingnexus.entity.Tenant;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(
            EntityManager entityManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        Tenant tenant = Tenant.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password())) // never store plain text
                .role(request.role())
                .build();

        entityManager.persist(tenant);
        entityManager.flush(); // forces the INSERT now, so tenant.getId() is populated below

        String token = jwtService.generateToken(tenant.getId(), tenant.getEmail(), tenant.getRole().name());

        return ResponseEntity.ok(new AuthResponse(
                token, tenant.getId(), tenant.getEmail(), tenant.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Tenant tenant = userDetailsService.loadTenantByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), tenant.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(tenant.getId(), tenant.getEmail(), tenant.getRole().name());

        return ResponseEntity.ok(new AuthResponse(
                token, tenant.getId(), tenant.getEmail(), tenant.getRole().name()));
    }
}