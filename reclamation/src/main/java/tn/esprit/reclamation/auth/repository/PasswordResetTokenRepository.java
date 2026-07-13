package tn.esprit.reclamation.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.reclamation.auth.entity.PasswordResetToken;
import tn.esprit.reclamation.Entity.Utilisateur;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUtilisateur(Utilisateur utilisateur);
}
