package com.matiasmeira.back_reservas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.matiasmeira.back_reservas.auth.security.JwtAuthenticationFilter;
import com.matiasmeira.back_reservas.auth.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

/**
 * Configuración de Spring Security 6 con JWT.
 * Define la cadena de filtros, autorización y gestión de sesiones.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Codificador de contraseñas BCrypt.
     * Se usa para codificar y verificar contraseñas.
     *
     * @return PasswordEncoder con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación DAO.
     * Utiliza UserDetailsService para cargar usuarios y compara contraseñas.
     *
     * @return DaoAuthenticationProvider configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Gestor de autenticación.
     * Se usa en AuthService para autenticar usuarios.
     *
     * @param config Configuración de autenticación
     * @return AuthenticationManager
     * @throws Exception Si hay error durante la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Cadena de filtros de seguridad principal.
     * Configura:
     * - CSRF deshabilitado (API stateless)
     * - Sesiones STATELESS
     * - Permisos por endpoint
     * - Inyección del filtro JWT
     *
     * @param http HttpSecurity para configurar
     * @return SecurityFilterChain configurada
     * @throws Exception Si hay error durante la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (API REST stateless)
                .csrf(csrf -> csrf.disable())

                // Gestión de sesiones stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configurar permisos de endpoints
                .authorizeHttpRequests(authorize -> authorize
                        // Públicos: Autenticación
                        .requestMatchers("/api/auth/**").permitAll()

                        // Públicos: Webhook de Mercado Pago (CRÍTICO)
                        .requestMatchers("POST", "/api/pagos/webhook").permitAll()

                        // Públicos: Listar establecimientos y ver disponibilidad
                        .requestMatchers("GET", "/api/establecimientos").permitAll()
                        .requestMatchers("GET", "/api/establecimientos/**").permitAll()
                        .requestMatchers("GET", "/api/booking/disponibilidad").permitAll()

                        // Dueños: Crear establecimiento
                        .requestMatchers("POST", "/api/establecimientos").hasRole("DUENIO")
                        .requestMatchers("PUT", "/api/establecimientos/**").hasRole("DUENIO")
                        .requestMatchers("DELETE", "/api/establecimientos/**").hasRole("DUENIO")

                        // Dueños: Acceso a POS
                        .requestMatchers("GET", "/api/pos/**").hasAnyRole("DUENIO", "ADMIN")
                        .requestMatchers("POST", "/api/pos/**").hasAnyRole("DUENIO", "ADMIN")

                        // Clientes: Crear y gestionar reservas
                        .requestMatchers("POST", "/api/booking/reservar").hasRole("CLIENTE")
                        .requestMatchers("GET", "/api/booking/mis-reservas").hasRole("CLIENTE")
                        .requestMatchers("POST", "/api/booking/**").hasRole("CLIENTE")

                        // Admin: Reintentar pagos
                        .requestMatchers("POST", "/api/pagos/**/reintentar").hasRole("ADMIN")

                        // Cualquier otro endpoint requiere autenticación
                        .anyRequest().authenticated()
                )

                // Proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                // Inyectar filtro JWT antes del filtro de usuario/contraseña
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
