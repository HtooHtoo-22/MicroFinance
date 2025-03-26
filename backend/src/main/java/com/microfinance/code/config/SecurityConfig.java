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

    private static final String[] SME_LIST_URL = {
            "/api/sme-loans/**",
            "/api/sme-loans/monthly-approved",
            "/api/sme-loans/loans/{branchId}" // Add this temporarily
    };

    private  static final String[] HP_LIST_URL = {
            "/api/hp-loans/**",
            "/api/hp-loans/list",
            "/api/hp-loans-schedule/**",
            "/api/hp-loans/monthly-approved"
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
                                .requestMatchers("/topic/hp-loans/**").permitAll()
                                .requestMatchers("/topic/hp-loan-status/**").permitAll()
                                .requestMatchers("/topic/sme-loans/**").permitAll()
                                .requestMatchers("/topic/sme-loan-status/**").permitAll()
                                .requestMatchers("/ws/**").permitAll() // Allow WebSocket
                                .requestMatchers("/api/cif/**").hasAnyAuthority( "CIF_READ")
                                .requestMatchers("/api/cif").hasAnyAuthority( "CIF_WRITE")
                                .requestMatchers("/api/users/**").hasAnyAuthority( "USER_WRITE")
                                .requestMatchers("/api/users").hasAnyAuthority("USER_CREATE")
                                .requestMatchers("/rates/**").hasAnyAuthority( "RATE_READ")
                                .requestMatchers("/rates").hasAnyAuthority( "RATE_CREATE")
                                .requestMatchers("/api/branches/**").hasAnyAuthority( "BRANCH_READ", "BRANCH_WRITE")
                                .requestMatchers("/api/roles/**").hasAnyAuthority( "ROLE_READ")
                                .requestMatchers("/api/roles").hasAnyAuthority( "ROLE_WRITE", "ADMIN")
                                .requestMatchers("/api/collateral-types/**").hasAnyAuthority("COLLATERAL_TYPE_READ", "COLLATERAL_TYPE_WRITE")
                                .requestMatchers("/accounts/**").hasAnyAuthority("ACCOUNT_READ")
                                .requestMatchers("/accounts").hasAnyAuthority("ACCOUNT_WRITE")
                                .requestMatchers("/api/dealers/create").hasAnyAuthority("DEALER_WRITE")
                                .requestMatchers(DEALER_LIST_URL).hasAnyAuthority("DEALER_READ")
                                .requestMatchers("/transactions/**").hasAnyAuthority("TRANSACTION_READ", "TRANSACTION_WRITE")
                                .requestMatchers("/api/collaterals/**").hasAnyAuthority("COLLATERAL_WRITE", "COLLATERAL_READ")
                                .requestMatchers("/api/products").hasAnyAuthority("PRODUCT_READ")
                                .requestMatchers("/api/products/**").hasAnyAuthority("PRODUCT_WRITE")
                                .requestMatchers("/api/hp-loans/register").hasAnyAuthority("HP_LOAN_WRITE")
                                .requestMatchers(HP_LIST_URL).hasAnyAuthority("HP_LOAN_READ")
                                .requestMatchers(SME_LIST_URL).hasAnyAuthority("SME_LOAN_WRITE", "SME_LOAN_READ")
                                .requestMatchers("/api/permission/**").hasAnyAuthority("PERMISSION_READ")
                                .requestMatchers("/api/dashboard/loan-metrics").permitAll()
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