package eadl.productgestion.services;

import eadl.productgestion.dto.request.UserRequest;
import eadl.productgestion.dto.response.UserResponse;
import eadl.productgestion.utils.UserAccountStatus;

import java.util.List;

/**
 * Contrat de service pour la gestion des utilisateurs.
 */
public interface UserService {

    /**
     * Enregistre un nouvel utilisateur.
     *
     * @param request données de l'utilisateur à créer
     * @return l'utilisateur créé
     */
    UserResponse create(UserRequest request);

    /**
     * Recherche un utilisateur par son identifiant.
     *
     * @param id identifiant de l'utilisateur
     * @return l'utilisateur correspondant
     */
    UserResponse findById(Long id);

    /**
     * Recherche un utilisateur par son adresse email.
     *
     * @param email adresse email de l'utilisateur
     * @return l'utilisateur correspondant
     */
    UserResponse findByEmail(String email);

    /**
     * Récupère la liste de tous les utilisateurs.
     *
     * @return liste des utilisateurs
     */
    List<UserResponse> findAll();

    /**
     * Met à jour les informations d'un utilisateur existant.
     *
     * @param id identifiant de l'utilisateur à modifier
     * @param request nouvelles données de l'utilisateur
     * @return l'utilisateur mis à jour
     */
    UserResponse update(Long id, UserRequest request);

    /**
     * Modifie le statut du compte d'un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @param status nouveau statut du compte
     * @return l'utilisateur mis à jour
     */
    UserResponse changeStatus(Long id, UserAccountStatus status);

    /**
     * Supprime un utilisateur par son identifiant.
     *
     * @param id identifiant de l'utilisateur à supprimer
     */
    void deleteById(Long id);
}
