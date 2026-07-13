package tn.esprit.reclamation.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.reclamation.auth.entity.PasswordResetToken;
import tn.esprit.reclamation.auth.repository.PasswordResetTokenRepository;
import tn.esprit.reclamation.Entity.Utilisateur;
import tn.esprit.reclamation.Repository.RepositoryUtilisateur;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements IPasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RepositoryUtilisateur repositoryUtilisateur;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Transactional
    public void forgotPassword(String email) {
        Utilisateur utilisateur = repositoryUtilisateur.findAll().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun compte trouvé avec cet email : " + email));

        // Supprimer un éventuel token existant
        passwordResetTokenRepository.deleteByUtilisateur(utilisateur);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .utilisateur(utilisateur)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(utilisateur.getEmail());
        message.setSubject("Réinitialisation de mot de passe");
        message.setText("Bonjour " + utilisateur.getUsername() + ",\n\n"
                + "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n"
                + resetLink + "\n\n"
                + "Ce lien expire dans 30 minutes.\n"
                + "Si vous n'avez rien demandé, ignorez cet email.");
        mailSender.send(message);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 6 caractères.");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide ou déjà utilisé."));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Le token a expiré. Veuillez redemander un lien.");
        }

        Utilisateur utilisateur = resetToken.getUtilisateur();
        utilisateur.setPassword(passwordEncoder.encode(newPassword));
        repositoryUtilisateur.save(utilisateur);

        passwordResetTokenRepository.delete(resetToken);
    }
}
