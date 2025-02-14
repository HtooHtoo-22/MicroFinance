package com.microfinance.code.auth;

import com.microfinance.code.model.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;  // Ensure Role is defined correctly
}