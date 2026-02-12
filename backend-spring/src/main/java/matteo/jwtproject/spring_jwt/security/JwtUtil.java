package matteo.jwtproject.spring_jwt.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    // Recupero la chiave segreta dal file application.properties
    // e la uso per firmare e verificare i token JWT
    @Value("${jwt.secret}")
    private String secret;

    // Recupero il tempo di scadenza del token (in millisecondi)
    @Value("${jwt.expiration}")
    private long expiration;

    // Questo metodo genera un token JWT a partire dallo username
    public String generateToken(String username) {

        // Creo e restituisco un nuovo token JWT
        return Jwts.builder()
                // Imposto lo username come soggetto del token
                .setSubject(username)

                // Imposto la data di creazione del token
                .setIssuedAt(new Date())

                // Imposto la data di scadenza del token
                .setExpiration(new Date(System.currentTimeMillis() + expiration))

                // Firmo il token usando la chiave segreta e l'algoritmo HMAC
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))

                // Converto il token in stringa e lo restituisco
                .compact();
    }

    // Questo metodo estrae lo username contenuto nel token JWT
    public String extractUsername(String token) {

        // Verifico il token usando la chiave segreta
        // e recupero il subject (username) dal corpo del token
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
