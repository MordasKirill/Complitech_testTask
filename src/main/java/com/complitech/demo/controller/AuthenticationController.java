package com.complitech.demo.controller;

import com.complitech.demo.entity.AuthenticationRequest;
import com.complitech.demo.entity.AuthenticationResponse;
import com.complitech.demo.util.JwtUtil;
import com.complitech.demo.util.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
public class AuthenticationController {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(JwtUtil jwtUtil,
                                    TokenBlacklistService tokenBlacklistService,
                                    AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);
        log.info("User with username '{}' logged in successfully.", userDetails.getUsername());
        return new AuthenticationResponse(jwt);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtUtil.claimTokenFromRequest(request);
        String username = jwtUtil.extractUsername(token);
        tokenBlacklistService.blacklistToken(token);
        log.info("User '{}' logged out successfully.", username);
        return ResponseEntity.ok("Logged out successfully");
    }
}
