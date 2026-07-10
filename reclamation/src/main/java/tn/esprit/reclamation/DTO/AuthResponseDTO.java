package tn.esprit.reclamation.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private Long id;
    private String username;
    private String email;
    private String role;
}
