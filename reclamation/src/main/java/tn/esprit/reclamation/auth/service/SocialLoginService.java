package tn.esprit.reclamation.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service de connexion sociale (Google OAuth).
 * Non utilisé actuellement dans cette application.
 * À implémenter en ajoutant la dépendance google-api-client dans pom.xml.
 */
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    public void socialLogin(String provider, String token) {
        throw new UnsupportedOperationException("La connexion sociale n'est pas encore configurée.");
    }
}
