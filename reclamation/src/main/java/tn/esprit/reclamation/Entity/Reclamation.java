package tn.esprit.reclamation.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Reclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    private CategorieReclamation categorie;
    @Enumerated(EnumType.STRING)
    private StatutReclamation statut;
    private LocalDateTime dateCreation ;
    private LocalDateTime dateModification;
    private String commentaireAdmin;
    @ManyToOne @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
