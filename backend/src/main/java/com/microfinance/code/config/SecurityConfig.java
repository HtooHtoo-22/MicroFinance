package com.microfinance.code.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/authenticate",
            "/api/v1/auth/refresh-token",
            "/ws/**",
            "/accounts/count/{branchId}",
    };

    private  static final String[] DEALER_LIST_URL = {
            "/api/dealers/{dealerId}/approve",
            "/api/dealers/{dealerId}/reject",
            "/api/dealers/list"
    };

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain createFilter(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Enable CORS in Spring Security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL).permitAll()
                                .requestMatchers("/api/cif/**").hasAnyAuthority( "ENTRY", "MANAGER")
                                .requestMatchers("/api/cif").hasAnyAuthority( "ENTRY")
                                .requestMatchers("/api/users/**").hasAnyAuthority( "ADMIN", "ENTRY", "MANAGER", "OPERATION")
                                .requestMatchers("/api/users").hasAnyAuthority("ADMIN")
                                .requestMatchers("/rates/**").hasAnyAuthority( "ADMIN")
                                .requestMatchers("/rates").hasAnyAuthority( "ADMIN")
                                .requestMatchers("/api/branches/**").hasAnyAuthority( "ADMIN","MANAGER", "ENTRY", "OPERATION")
                                .requestMatchers("/api/roles/**").hasAnyAuthority( "ADMIN")
                                .requestMatchers("/api/roles").hasAnyAuthority( "ADMIN")
                                .requestMatchers("/api/collateral-types/**").hasAnyAuthority("ADMIN")
                                .requestMatchers("/api/collateral-types").hasAnyAuthority("ADMIN")
                                .requestMatchers("/api/collateral-types/**").hasAnyAuthority("ADMIN")
                                .requestMatchers("/accounts/**").hasAnyAuthority("ENTRY")
                                .requestMatchers("/accounts").hasAnyAuthority("ENTRY", "ADMIN")
                                .requestMatchers("/api/dealers/create").hasAnyAuthority("ENTRY")
                                .requestMatchers(DEALER_LIST_URL).hasAnyAuthority("MANAGER")
                                .requestMatchers("/transactions/**").hasAnyAuthority("ENTRY")

                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        return http.build();
    }
}