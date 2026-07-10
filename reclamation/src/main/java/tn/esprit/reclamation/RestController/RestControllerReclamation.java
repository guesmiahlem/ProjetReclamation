package tn.esprit.reclamation.RestController;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.reclamation.DTO.ReclamationRequestDTO;
import tn.esprit.reclamation.DTO.ReclamationResponseDTO;
import tn.esprit.reclamation.DTO.ReclamationStatusUpdateDTO;
import tn.esprit.reclamation.Services.IReclamation;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/reclamations")
public class RestControllerReclamation {

    private  IReclamation reclamationService;

    @PostMapping("/add")
    public ResponseEntity<ReclamationResponseDTO> createReclamation(@RequestBody ReclamationRequestDTO requestDTO) {
        ReclamationResponseDTO created = reclamationService.createReclamation(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReclamationResponseDTO> getReclamationById(@PathVariable Long id) {
        return ResponseEntity.ok(reclamationService.getReclamationById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReclamationResponseDTO>> getAllReclamations() {
        return ResponseEntity.ok(reclamationService.getAllReclamations());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReclamationResponseDTO> updateReclamation(
            @PathVariable Long id,
            @RequestBody ReclamationRequestDTO requestDTO) {
        return ResponseEntity.ok(reclamationService.updateReclamation(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReclamation(@PathVariable Long id) {
        reclamationService.deleteReclamation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReclamationResponseDTO> updateReclamationStatus(
            @PathVariable Long id,
            @RequestBody ReclamationStatusUpdateDTO statusUpdateDTO) {
        return ResponseEntity.ok(reclamationService.updateReclamationStatus(id, statusUpdateDTO));
    }
}
