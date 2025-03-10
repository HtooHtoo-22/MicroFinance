package com.microfinance.code.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("branchId")
    private String branchId;
    @JsonProperty("role")
    private String role;
}