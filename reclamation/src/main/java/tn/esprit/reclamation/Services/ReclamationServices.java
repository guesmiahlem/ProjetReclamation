package tn.esprit.reclamation.Services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.reclamation.DTO.ReclamationRequestDTO;
import tn.esprit.reclamation.DTO.ReclamationResponseDTO;
import tn.esprit.reclamation.DTO.ReclamationStatusUpdateDTO;
import tn.esprit.reclamation.Entity.Reclamation;
import tn.esprit.reclamation.Entity.StatutReclamation;
import tn.esprit.reclamation.Entity.Utilisateur;
import tn.esprit.reclamation.Mapper.ReclamationMapper;
import tn.esprit.reclamation.Repository.RepositoryReclamation;
import tn.esprit.reclamation.Repository.RepositoryUtilisateur;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReclamationServices implements IReclamation {

    private  RepositoryReclamation repositoryReclamation;
    private  RepositoryUtilisateur repositoryUtilisateur;
    private  ReclamationMapper reclamationMapper;

    @Override
    public ReclamationResponseDTO createReclamation(ReclamationRequestDTO requestDTO) {
        Utilisateur user = null;
        if (requestDTO.getUtilisateurId() != null) {
            user = repositoryUtilisateur.findById(requestDTO.getUtilisateurId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'ID: " + requestDTO.getUtilisateurId()));
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserDetails) {
                    user = ((CustomUserDetails) principal).getUtilisateur();
                } else {
                    String currentUsername = authentication.getName();
                    user = repositoryUtilisateur.findByUsername(currentUsername).orElse(null);
                }
            }
        }

        Reclamation reclamation = reclamationMapper.toEntity(requestDTO);
        reclamation.setStatut(StatutReclamation.EN_ATTENTE);
        reclamation.setDateCreation(LocalDateTime.now());
        reclamation.setUtilisateur(user);

        Reclamation saved = repositoryReclamation.save(reclamation);
        return reclamationMapper.toResponseDTO(saved);
    }

    @Override
    public ReclamationResponseDTO getReclamationById(Long id) {
        Reclamation reclamation = repositoryReclamation.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Réclamation non trouvée avec l'ID: " + id));
        return reclamationMapper.toResponseDTO(reclamation);
    }

    @Override
    public List<ReclamationResponseDTO> getAllReclamations() {
        return repositoryReclamation.findAll().stream()
                .map(reclamationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReclamationResponseDTO updateReclamation(Long id, ReclamationRequestDTO requestDTO) {
        Reclamation existing = repositoryReclamation.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Réclamation non trouvée avec l'ID: " + id));

        existing.setTitre(requestDTO.getTitre());
        existing.setDescription(requestDTO.getDescription());
        existing.setCategorie(requestDTO.getCategorie());
        existing.setDateModification(LocalDateTime.now());

        if (requestDTO.getUtilisateurId() != null) {
            Utilisateur user = repositoryUtilisateur.findById(requestDTO.getUtilisateurId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'ID: " + requestDTO.getUtilisateurId()));
            existing.setUtilisateur(user);
        }

        Reclamation saved = repositoryReclamation.save(existing);
        return reclamationMapper.toResponseDTO(saved);
    }

    @Override
    public void deleteReclamation(Long id) {
        if (!repositoryReclamation.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Réclamation non trouvée avec l'ID: " + id);
        }
        repositoryReclamation.deleteById(id);
    }

    @Override
    public ReclamationResponseDTO updateReclamationStatus(Long id, ReclamationStatusUpdateDTO statusUpdateDTO) {
        Reclamation existing = repositoryReclamation.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Réclamation non trouvée avec l'ID: " + id));

        existing.setStatut(statusUpdateDTO.getStatut());
        existing.setCommentaireAdmin(statusUpdateDTO.getCommentaireAdmin());
        existing.setDateModification(LocalDateTime.now());

        Reclamation saved = repositoryReclamation.save(existing);
        return reclamationMapper.toResponseDTO(saved);
    }
}
