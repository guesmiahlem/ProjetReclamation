package tn.esprit.reclamation.DTO;

import lombok.*;
import tn.esprit.reclamation.Entity.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    private String username;
    private String email;
    private String password;
    private Role role;
}
