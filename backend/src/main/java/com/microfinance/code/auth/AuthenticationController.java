package com.microfinance.code.auth;


import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.model.Role;
import com.microfinance.code.model.User;
import com.microfinance.code.repository.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final RoleRepository roleRepository;


    @PostMapping("/admin/user")
    public ResponseEntity<AuthenticationResponse> createUser(
            @RequestBody RegisterRequest request,
            @RequestParam String role
    ) throws BadRequestException {
        Role userRole = roleRepository.findByRoleName(role)
                .orElseThrow(() -> new NotFoundException("Role is not found."));
        request.setRole(userRole);
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public  void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
        SecurityContextHolder.clearContext(); // Ensure context is cleared
        return ResponseEntity.ok("Logout successful");
    }

}
