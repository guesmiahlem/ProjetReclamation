package tn.esprit.reclamation.DTO;

import lombok.*;
import tn.esprit.reclamation.Entity.CategorieReclamation;
import tn.esprit.reclamation.Entity.StatutReclamation;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamationResponseDTO {
    private Long id;
    private String titre;
    private String description;
    private CategorieReclamation categorie;
    private StatutReclamation statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String commentaireAdmin;
    private Long utilisateurId;
    private String utilisateurUsername;
}
