package tn.esprit.reclamation.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.reclamation.Entity.Reclamation;

@Repository
public interface RepositoryReclamation extends JpaRepository<Reclamation, Long> {
}
