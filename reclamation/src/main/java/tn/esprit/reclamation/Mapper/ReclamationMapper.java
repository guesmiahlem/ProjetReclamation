package tn.esprit.reclamation.Mapper;

import org.springframework.stereotype.Component;
import tn.esprit.reclamation.DTO.ReclamationRequestDTO;
import tn.esprit.reclamation.DTO.ReclamationResponseDTO;
import tn.esprit.reclamation.Entity.Reclamation;

@Component
public class ReclamationMapper {

    public Reclamation toEntity(ReclamationRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        Reclamation reclamation = new Reclamation();
        reclamation.setTitre(requestDTO.getTitre());
        reclamation.setDescription(requestDTO.getDescription());
        reclamation.setCategorie(requestDTO.getCategorie());
        return reclamation;
    }

    public ReclamationResponseDTO toResponseDTO(Reclamation reclamation) {
        if (reclamation == null) {
            return null;
        }
        return ReclamationResponseDTO.builder()
                .id(reclamation.getId())
                .titre(reclamation.getTitre())
                .description(reclamation.getDescription())
                .categorie(reclamation.getCategorie())
                .statut(reclamation.getStatut())
                .dateCreation(reclamation.getDateCreation())
                .dateModification(reclamation.getDateModification())
                .commentaireAdmin(reclamation.getCommentaireAdmin())
                .utilisateurId(reclamation.getUtilisateur() != null ? reclamation.getUtilisateur().getId() : null)
                .utilisateurUsername(reclamation.getUtilisateur() != null ? reclamation.getUtilisateur().getUsername() : null)
                .build();
    }
}
