package eadl.productgestion.services;

import eadl.productgestion.dto.request.LoginRequestDTO;
import eadl.productgestion.dto.request.RegisterRequestDTO;
import eadl.productgestion.dto.response.AuthResponse;

/**
 * Contrat de service pour l'authentification et l'enregistrement des utilisateurs.
 */
public interface AuthService {

    /**
     * Authentifie un utilisateur avec ses identifiants.
     *
     * @param request informations de connexion de l'utilisateur
     * @return les informations d'authentification avec le jeton d'accès
     */
    AuthResponse login(LoginRequestDTO request);

    /**
     * Enregistre un nouvel utilisateur dans l'application.
     *
     * @param request informations d'inscription de l'utilisateur
     * @return les informations d'authentification générées après inscription
     */
    AuthResponse register(RegisterRequestDTO request);

    /**
     * Déconnecte un utilisateur en invalidant son jeton d'accès.
     *
     * @param token jeton d'accès à révoquer
     */
    void logout(String token);
}
