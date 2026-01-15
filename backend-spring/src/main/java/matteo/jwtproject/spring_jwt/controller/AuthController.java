package matteo.jwtproject.spring_jwt.controller;

import matteo.jwtproject.spring_jwt.model.Role;
import matteo.jwtproject.spring_jwt.model.User;
import matteo.jwtproject.spring_jwt.repository.UserRepository;
import matteo.jwtproject.spring_jwt.security.JwtUtil;

import java.util.Collections;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Username e password richiesti");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Username già esistente");
        }

        if (user.getPassword().length() < 3) {
            return ResponseEntity
                    .badRequest()
                    .body("La password deve contenere almeno 3 caratteri");
        } else
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

        return ResponseEntity.ok("Utente registrato con successo");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        // Controlli base
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Username e password richiesti");
        }

        // Cerca l'utente nel DB
        User dbUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Controlla la password
        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body("Password errata");
        }

        // Genera token se tutto ok
        String token = jwtUtil.generateToken(dbUser.getUsername());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logout effettuato con successo");
    }
}
