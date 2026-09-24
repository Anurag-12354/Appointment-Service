package com.anurag.appointmentbooking.Security;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationProvider authenticationProvider(
                        CustomUserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

                provider.setPasswordEncoder(passwordEncoder);

                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationProvider authenticationProvider) {
                return new ProviderManager(authenticationProvider);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        AuthenticationProvider authenticationProvider) throws Exception {

                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authenticationProvider(authenticationProvider)
                                .authorizeHttpRequests(authorize -> authorize

                                                .dispatcherTypeMatchers(
                                                                DispatcherType.ERROR)
                                                .permitAll()

                                                .requestMatchers(
                                                                "/api/auth/register",
                                                                "/api/auth/login")
                                                .permitAll()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/doctors/**",
                                                                "/api/services/**")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/api/doctors",
                                                                "/api/doctors/**",
                                                                "/api/services",
                                                                "/api/services/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(
                                                                "/api/appointments/**")
                                                .authenticated()
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/appointments",
                                                                "/api/appointments/")
                                                .hasRole("ADMIN")

                                                .requestMatchers("/api/appointments/**")
                                                .authenticated()

                                                .anyRequest().authenticated())
                                .httpBasic(Customizer.withDefaults())
                                .build();
        }
}
