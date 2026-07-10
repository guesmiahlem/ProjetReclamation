package tn.esprit.reclamation.DTO;

import lombok.*;
import tn.esprit.reclamation.Entity.StatutReclamation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamationStatusUpdateDTO {
    private StatutReclamation statut;
    private String commentaireAdmin;
}
