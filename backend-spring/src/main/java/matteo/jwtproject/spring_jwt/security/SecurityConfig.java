package matteo.jwtproject.spring_jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/*
Questa classe:
-definisce le regole
-registra il filtro JWT
-imposta backend stateless
-gestisce CORS e sicurezza
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, CustomUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // <-- CORS abilitato
                .csrf(csrf -> csrf.disable()) // Disabilito CSRF perché uso JWT, stateless non uso cookie di sessione
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/h2-console/**")
                        .permitAll()
                        .anyRequest().authenticated() // Chiunque può accedere a:/auth/login /auth/register
                                                      // /h2-console/** TUTTO il resto: solo se autenticato (JWT valido)
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Serve SOLO per far funzionare la
                                                                                    // console H2 nel browser
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // NON usare
                                                                                                         // sessioni,
                                                                                                         // MAI

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Inserisco il mio filtro JWT
                                                                                     // PRIMA del filtro standard
                                                                                     // username/password

        return http.build();
    }

    // Bean per configurare CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200")); // frontend Angular
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    } /*
       * solo http://localhost:4200 può chiamare il backend
       * metodi permessi: GET, POST, PUT, DELETE, OPTIONS
       * tutti gli header permessi
       * credenziali consentite
       * È per permettere ad Angular di chiamare Spring
       */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*
     * Serve per:
     * hashare le password nel database
     * confrontare password al login
     * Mai salvare password in chiaro
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    /*
     * Serve per:
     * gestire il login
     * confrontare username + password
     */
}
