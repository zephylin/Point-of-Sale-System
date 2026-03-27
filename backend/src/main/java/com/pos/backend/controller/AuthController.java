package com.pos.backend.controller;

import com.pos.backend.domain.Cashier;
import com.pos.backend.dto.AuthDTO;
import com.pos.backend.repository.CashierRepository;
import com.pos.backend.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CashierRepository cashierRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.LoginResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getCashierNumber(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            Cashier cashier = cashierRepository.findByNumber(request.getCashierNumber())
                    .orElseThrow();

            String name = cashier.getPerson() != null
                    ? cashier.getPerson().getFirstName() + " " + cashier.getPerson().getLastName()
                    : cashier.getNumber();

            return ResponseEntity.ok(new AuthDTO.LoginResponse(
                    token,
                    cashier.getNumber(),
                    name,
                    cashier.getRole()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
