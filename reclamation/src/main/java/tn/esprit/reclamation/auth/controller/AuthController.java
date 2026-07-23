package tn.esprit.reclamation.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.reclamation.DTO.AuthRequest;
import tn.esprit.reclamation.DTO.AuthResponse;
import tn.esprit.reclamation.DTO.ResetPasswordRequest;
import tn.esprit.reclamation.auth.util.JwtUtil;
import tn.esprit.reclamation.Entity.Utilisateur;
import tn.esprit.reclamation.Entity.Role;
import tn.esprit.reclamation.Repository.RepositoryUtilisateur;
import tn.esprit.reclamation.auth.service.IPasswordResetService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RepositoryUtilisateur repositoryUtilisateur;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final IPasswordResetService passwordResetService;

    // ========== HELPER: Set JWT as HttpOnly Cookie ==========
    private void setJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) // Set true in production when using HTTPS.
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    // ========== HELPER: Clear JWT Cookie on logout ==========
    private void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Utilisateur user, HttpServletResponse response) {
        if (repositoryUtilisateur.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nom d'utilisateur déjà utilisé.");
        }
        if (repositoryUtilisateur.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email déjà utilisé.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.User);
        }

        Utilisateur savedUser = repositoryUtilisateur.save(user);
        final String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole().name());
        
        // Set token in HttpOnly cookie
        setJwtCookie(response, token);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .name(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("error", "Votre compte est désactivé. Veuillez contacter le support."));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Nom d'utilisateur ou mot de passe incorrect."));
        }

        Utilisateur user = repositoryUtilisateur.findByUsername(request.getUsername())
                .or(() -> repositoryUtilisateur.findByEmail(request.getUsername()))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        final String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        
        // Set token in HttpOnly cookie
        setJwtCookie(response, token);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .name(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<java.util.Map<String, String>> logout(HttpServletResponse response) {
        clearJwtCookie(response);
        return ResponseEntity.ok(java.util.Map.of("message", "Déconnexion réussie."));
    }

    // ========== FORGOT PASSWORD ==========
    @PostMapping("/forgot-password")
    public ResponseEntity<java.util.Map<String, String>> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "L'adresse email est obligatoire."));
        }
        try {
            passwordResetService.forgotPassword(email);
            return ResponseEntity.ok(java.util.Map.of(
                    "message", "Un email de réinitialisation a été envoyé à votre adresse email."));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    // ========== RESET PASSWORD ==========
    @PostMapping("/reset-password")
    public ResponseEntity<java.util.Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(java.util.Map.of(
                    "message", "Votre mot de passe a été réinitialisé avec succès."));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}

