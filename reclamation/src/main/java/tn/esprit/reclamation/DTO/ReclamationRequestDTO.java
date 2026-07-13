package tn.esprit.reclamation.DTO;

import lombok.*;
import tn.esprit.reclamation.Entity.CategorieReclamation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamationRequestDTO {
    private String titre;
    private String description;
    private CategorieReclamation categorie;
    private Long utilisateurId;



}
