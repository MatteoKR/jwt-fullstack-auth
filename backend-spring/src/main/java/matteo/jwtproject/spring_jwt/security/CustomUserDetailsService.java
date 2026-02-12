package matteo.jwtproject.spring_jwt.security;

import matteo.jwtproject.spring_jwt.model.User;
import matteo.jwtproject.spring_jwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Uso il repository per accedere agli utenti salvati nel database
    private final UserRepository userRepository;

    // Inietto il UserRepository tramite costruttore
    // così Spring può fornirmi automaticamente l'istanza corretta
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Questo metodo viene chiamato automaticamente da Spring Security ogni volta che un utente tenta di autenticarsi
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Cerco l'utente nel database usando lo username
        // Se non lo trovo, lancio un'eccezione e blocco il login
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        // Converto l'utente del database in un oggetto UserDetails
        // che Spring Security può usare per l'autenticazione
        return org.springframework.security.core.userdetails.User.builder()
                // Imposto lo username dell'utente
                .username(user.getUsername())

                // Imposto la password (che deve essere già criptata)
                .password(user.getPassword())

                // Imposto il ruolo dell'utente
                // Rimuovo il prefisso ROLE_ perché Spring lo aggiunge automaticamente
                .roles(user.getRole().name().replace("ROLE_", ""))

                // Costruisco e restituisco l'oggetto UserDetails
                .build();
    }
}
