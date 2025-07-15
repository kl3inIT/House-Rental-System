package com.rental.houserental.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Value("${app.security.remember-me.key}")
    private String rememberMeKey;

    @Value("${app.security.remember-me.validity}")
    private int rememberMeValidity;

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String redirectUrl = "/";

            // Check user roles and redirect accordingly
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            boolean isLandlord = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_LANDLORD"));

            if (isAdmin) {
                redirectUrl = "/admin/dashboard";
            } else if (isLandlord) {
                redirectUrl = "/landlord/dashboard";
            } else {
                redirectUrl = "/"; // Default for USER role
            }

            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String redirectUrl = "/login?error=true";

            if (exception instanceof DisabledException) {
                redirectUrl = "/login?disabled=true";
            } else if (exception instanceof LockedException) {
                redirectUrl = "/login?locked=true";
            } else if (exception instanceof BadCredentialsException) {
                redirectUrl = "/login?error=true";
            }

            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CSRF protection for better security
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/wallet/deposit/webhook")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/landlord/**").hasRole("LANDLORD")
                        .requestMatchers("/user/**").hasAnyRole("USER", "LANDLORD", "ADMIN")
                        .requestMatchers("/landlord-upgrade-requests", "/landlord-upgrade-requests/**").hasRole("USER")
                        .requestMatchers("/", "/login", "/perform-login", "/register", "/verify-email/**",
                                "/forgot-password", "/reset-password/**", "/verify-otp/**", "/resend-otp",
                                "/css/**", "/js/**", "/images/**", "/output.css", "/wallet/deposit/webhook")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform-login") // Custom processing URL
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID", "remember-me")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll())
                .rememberMe(remember -> remember
                        .key(rememberMeKey)
                        .tokenValiditySeconds(rememberMeValidity)
                        .userDetailsService(userDetailsService)
                        .rememberMeParameter("remember-me"))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expired=true"))
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403"));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
