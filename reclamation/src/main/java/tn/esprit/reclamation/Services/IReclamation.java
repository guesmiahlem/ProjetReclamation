package tn.esprit.reclamation.Services;

import tn.esprit.reclamation.DTO.ReclamationRequestDTO;
import tn.esprit.reclamation.DTO.ReclamationResponseDTO;
import tn.esprit.reclamation.DTO.ReclamationStatusUpdateDTO;

import java.util.List;

public interface IReclamation {
    ReclamationResponseDTO createReclamation(ReclamationRequestDTO requestDTO);
    ReclamationResponseDTO getReclamationById(Long id);
    List<ReclamationResponseDTO> getAllReclamations();
    ReclamationResponseDTO updateReclamation(Long id, ReclamationRequestDTO requestDTO);
    void deleteReclamation(Long id);
    ReclamationResponseDTO updateReclamationStatus(Long id, ReclamationStatusUpdateDTO statusUpdateDTO);
}
