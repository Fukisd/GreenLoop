package org.greenloop.circularfashion.config;

import org.greenloop.circularfashion.security.JwtAuthenticationFilter;
import org.greenloop.circularfashion.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Allow preflight CORS requests
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/items/public/**").permitAll()
                        .requestMatchers("/api/marketplace/public/**").permitAll()
                        .requestMatchers("/api/collections/public/**").permitAll()
                        
                        // Allow public GET access to brands and categories
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/brands/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/items/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/items/*/statistics/**").permitAll()
                        
                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        
                        // Actuator and WebSocket
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/ws/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        
                        // User endpoints (require authentication for write operations)
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/items/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/items/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/items/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/items/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/brands/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/brands/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/brands/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers("/api/marketplace/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers("/api/collections/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers("/api/reviews/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers("/api/posts/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        .requestMatchers("/api/messages/**").hasAnyRole("USER", "ADMIN", "STAFF")
                        
                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/moderator/**").hasAnyRole("ADMIN", "STAFF")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
} 