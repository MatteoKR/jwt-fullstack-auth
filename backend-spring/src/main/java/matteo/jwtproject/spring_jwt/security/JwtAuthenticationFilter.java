package matteo.jwtproject.spring_jwt.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * Questa classe rappresenta un filtro di sicurezza personalizzato.
 * La uso per intercettare ogni richiesta HTTP e verificare la presenza
 * di un token JWT valido prima di permettere l’accesso alle risorse protette.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Uso JwtUtil per estrarre e validare le informazioni contenute nel token JWT
    private final JwtUtil jwtUtil;

    // Uso CustomUserDetailsService per recuperare i dettagli dell’utente dal database
    private final CustomUserDetailsService userDetailsService;

    // Inietto le dipendenze tramite costruttore
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Questo metodo viene eseguito automaticamente per ogni richiesta HTTP
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Recupero l'header Authorization dalla richiesta HTTP
        String authHeader = request.getHeader("Authorization");

        // Controllo che l'header esista e che inizi con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // Estraggo il token JWT rimuovendo il prefisso "Bearer "
            String token = authHeader.substring(7);

            // Dal token estraggo lo username dell’utente
            String username = jwtUtil.extractUsername(token);

            // Controllo che lo username non sia nullo
            // e che l’utente non sia già autenticato nel SecurityContext
            if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

                // Carico i dettagli dell’utente dal database
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                // Creo un token di autenticazione per Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,           // Utente autenticato
                                null,                  // Credenziali (non servono perché uso JWT)
                                userDetails.getAuthorities() // Ruoli e permessi
                        );

                // Associo alla richiesta i dettagli
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Imposto l’utente come autenticato nel SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Passo la richiesta al filtro successivo nella catena
        filterChain.doFilter(request, response);
    }
}
