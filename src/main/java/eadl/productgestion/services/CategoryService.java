package eadl.productgestion.services;

import eadl.productgestion.dto.request.CategoryRequest;
import eadl.productgestion.dto.response.CategoryResponse;

import java.util.List;

/**
 * Contrat de service pour la gestion des catégories.
 */
public interface CategoryService {

    /**
     * Crée une nouvelle catégorie.
     *
     * @param request données de la catégorie à créer
     * @return la catégorie enregistrée
     */
    CategoryResponse create(CategoryRequest request);

    /**
     * Récupère l'ensemble des catégories.
     *
     * @return la liste des catégories
     */
    List<CategoryResponse> findAll();

    /**
     * Recherche une catégorie par son identifiant.
     *
     * @param id identifiant de la catégorie
     * @return la catégorie correspondante
     */
    CategoryResponse findById(Long id);
}
