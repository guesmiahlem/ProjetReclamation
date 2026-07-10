package tn.esprit.reclamation.Services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tn.esprit.reclamation.Entity.Utilisateur;
import tn.esprit.reclamation.Repository.RepositoryUtilisateur;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RepositoryUtilisateur repositoryUtilisateur;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur user = repositoryUtilisateur.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le pseudo: " + username));
        return new CustomUserDetails(user);
    }

}
