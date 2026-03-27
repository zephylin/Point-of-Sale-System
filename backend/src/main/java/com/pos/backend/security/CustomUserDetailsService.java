package com.pos.backend.security;

import com.pos.backend.domain.Cashier;
import com.pos.backend.repository.CashierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CashierRepository cashierRepository;

    @Override
    public UserDetails loadUserByUsername(String cashierNumber) throws UsernameNotFoundException {
        Cashier cashier = cashierRepository.findByNumber(cashierNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Cashier not found: " + cashierNumber));

        if (!Boolean.TRUE.equals(cashier.getIsActive())) {
            throw new UsernameNotFoundException("Cashier account is inactive: " + cashierNumber);
        }

        String role = cashier.getRole() != null ? cashier.getRole().toUpperCase() : "CASHIER";

        return new User(
                cashier.getNumber(),
                cashier.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
