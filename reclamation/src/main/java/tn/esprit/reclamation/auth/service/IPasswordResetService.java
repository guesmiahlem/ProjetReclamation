package tn.esprit.reclamation.auth.service;

public interface IPasswordResetService {
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
