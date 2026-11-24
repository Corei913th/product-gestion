package eadl.productgestion.services;

import eadl.productgestion.dto.request.CategoryRequest;
import eadl.productgestion.dto.response.CategoryResponse;
import eadl.productgestion.exception.CategoryNotFoundException;
import eadl.productgestion.exception.DuplicateCategoryException;

import java.util.List;

/**
 * Service de gestion des catégories de produits.
 */
public interface CategoryService {

    /**
     * Crée une nouvelle catégorie après vérification de l'unicité de son nom.
     *
     * @param request données de la catégorie à créer
     * @return la catégorie créée
     * @throws DuplicateCategoryException si une catégorie avec le même nom existe déjà
     */
    CategoryResponse create(CategoryRequest request);

    /**
     * Retourne la liste de toutes les catégories existantes.
     *
     * @return liste des catégories
     */
    List<CategoryResponse> findAll();

    /**
     * Recherche une catégorie par son identifiant unique.
     *
     * @param id identifiant de la catégorie
     * @return la catégorie trouvée
     * @throws CategoryNotFoundException si aucune catégorie ne correspond à l'identifiant
     */
    CategoryResponse findById(Long id);
}
