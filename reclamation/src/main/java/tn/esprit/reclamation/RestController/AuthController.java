package tn.esprit.reclamation.RestController;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.reclamation.Config.JwtUtils;
import tn.esprit.reclamation.DTO.AuthResponseDTO;
import tn.esprit.reclamation.DTO.LoginRequestDTO;
import tn.esprit.reclamation.DTO.RegisterRequestDTO;
import tn.esprit.reclamation.Entity.Role;
import tn.esprit.reclamation.Entity.Utilisateur;
import tn.esprit.reclamation.Repository.RepositoryUtilisateur;
import tn.esprit.reclamation.Services.CustomUserDetails;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class AuthController {

    private  AuthenticationManager authenticationManager;
    private  RepositoryUtilisateur repositoryUtilisateur;
    private  PasswordEncoder passwordEncoder;
    private  JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        if (repositoryUtilisateur.existsByUsername(registerRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur: Le nom d'utilisateur est déjà pris!");
        }

        if (repositoryUtilisateur.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur: L'adresse email est déjà utilisée!");
        }

        Utilisateur user = new Utilisateur();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        
        if (registerRequest.getRole() == null) {
            user.setRole(Role.User);
        } else {
            user.setRole(registerRequest.getRole());
        }

        repositoryUtilisateur.save(user);

        return ResponseEntity.ok("Utilisateur enregistré avec succès!");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        Utilisateur user = userDetails.getUtilisateur();

        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(jwt)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : "User")
                .build();

        return ResponseEntity.ok(response);
    }
}
