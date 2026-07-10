package tn.esprit.reclamation.RestController;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.reclamation.DTO.UserResponseDTO;
import tn.esprit.reclamation.Entity.Utilisateur;
import tn.esprit.reclamation.Repository.RepositoryUtilisateur;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class UserController {

    private final RepositoryUtilisateur repositoryUtilisateur;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<Utilisateur> users = repositoryUtilisateur.findAll();
        List<UserResponseDTO> response = users.stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole() != null ? user.getRole().name() : "User")
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
