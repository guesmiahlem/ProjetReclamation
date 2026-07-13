package tn.esprit.reclamation.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tn.esprit.reclamation.Entity.Utilisateur;
import tn.esprit.reclamation.Entity.Role;
import tn.esprit.reclamation.Repository.RepositoryUtilisateur;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RepositoryUtilisateur repositoryUtilisateur;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!repositoryUtilisateur.existsByEmail("admin@gmail.com")) {
            Utilisateur admin = new Utilisateur();
            admin.setUsername("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(Role.Admin);
            repositoryUtilisateur.save(admin);
            System.out.println("✅ ADMIN account created: admin / Admin123!");
        } else {
            // Toujours réinitialiser le mot de passe admin pour garantir l'accès
            Utilisateur admin = repositoryUtilisateur.findByUsername("admin")
                    .orElse(null);
            if (admin != null) {
                admin.setPassword(passwordEncoder.encode("Admin123!"));
                admin.setRole(Role.Admin);
                repositoryUtilisateur.save(admin);
                System.out.println("🔄 ADMIN password reset to default: admin / Admin123!");
            } else {
                System.out.println("ℹ️ ADMIN email exists but username 'admin' not found.");
            }
        }
    }
}
