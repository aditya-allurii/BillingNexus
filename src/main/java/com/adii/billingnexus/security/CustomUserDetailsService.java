package com.adii.billingnexus.security;

import com.adii.billingnexus.entity.Tenant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EntityManager entityManager;

    public CustomUserDetailsService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Tenant loadTenantByEmail(String email) {
        TypedQuery<Tenant> query = entityManager.createQuery(
                "SELECT t FROM Tenant t WHERE t.email = :email", Tenant.class);
        query.setParameter("email", email);
        List<Tenant> results = query.getResultList();
        if (results.isEmpty()) {
            throw new UsernameNotFoundException("No tenant found with email: " + email);
        }
        return results.get(0);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Tenant tenant = loadTenantByEmail(email);
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + tenant.getRole().name());
        return new User(tenant.getEmail(), tenant.getPasswordHash(), List.of(authority));
    }
}