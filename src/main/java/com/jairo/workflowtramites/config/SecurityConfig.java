package com.jairo.workflowtramites.config;

import com.jairo.workflowtramites.security.CustomUserDetailsService;
import com.jairo.workflowtramites.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // -- Reglas por rol (descomentar cuando estés listo para activar la seguridad) --
                // .requestMatchers("/api/auth/**").permitAll()
                // .requestMatchers("/ws/**").permitAll()
                //
                // // Administración de catálogos y flujos: solo ADMIN
                // .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                // .requestMatchers("/api/departamentos/**").hasRole("ADMIN")
                // .requestMatchers("/api/tramites/**").hasRole("ADMIN")
                // .requestMatchers("/api/flujos/**").hasRole("ADMIN")
                // .requestMatchers("/api/flujos-trabajo/**").hasRole("ADMIN")
                // .requestMatchers("/api/formularios/**").hasRole("ADMIN")
                // .requestMatchers("/camunda/**").hasRole("ADMIN")
                //
                // // Bandejas y acciones de funcionario: FUNCIONARIO o ADMIN
                // .requestMatchers("/api/solicitudes/mi-departamento/**").hasAnyRole("FUNCIONARIO", "ADMIN")
                // .requestMatchers(org.springframework.http.HttpMethod.POST,
                //         "/api/solicitudes/*/tomar",
                //         "/api/solicitudes/*/liberar",
                //         "/api/solicitudes/*/respuesta-departamento").hasAnyRole("FUNCIONARIO", "ADMIN")
                //
                // // Resto de endpoints de solicitudes: cualquier autenticado (validación fina en el service)
                // .requestMatchers("/api/solicitudes/**").authenticated()
                //
                // // Analytics: FUNCIONARIO o ADMIN
                // .requestMatchers("/api/analytics/**").hasAnyRole("FUNCIONARIO", "ADMIN")
                //
                // .anyRequest().authenticated()
                // -----------------------------------------------------------------------
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
