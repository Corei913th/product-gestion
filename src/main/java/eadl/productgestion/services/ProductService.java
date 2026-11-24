package eadl.productgestion.services;

import eadl.productgestion.dto.request.ProductRequest;
import eadl.productgestion.dto.request.StockAdjustmentRequest;
import eadl.productgestion.dto.response.ProductResponse;
import eadl.productgestion.exception.CategoryNotFoundException;
import eadl.productgestion.exception.DuplicateProductException;
import eadl.productgestion.exception.InsufficientStockException;
import eadl.productgestion.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Service de gestion du catalogue de produits et des opérations de stock.
 */
public interface ProductService {

    /**
     * Crée un nouveau produit rattaché à une catégorie existante.
     *
     * @param request données du produit à créer
     * @return le produit créé
     * @throws DuplicateProductException si la référence SKU existe déjà
     * @throws CategoryNotFoundException si la catégorie indiquée n'existe pas
     */
    ProductResponse create(ProductRequest request);

    /**
     * Recherche un produit par son identifiant unique.
     *
     * @param id identifiant unique du produit
     * @return le produit trouvé
     * @throws ProductNotFoundException si aucun produit ne correspond à l'identifiant
     */
    ProductResponse findById(Long id);

    /**
     * Récupère une page de produits selon des filtres multicritères facultatifs.
     *
     * @param name filtre textuel partiel sur le nom
     * @param categoryId identifiant de la catégorie
     * @param minPrice prix unitaire minimum
     * @param maxPrice prix unitaire maximum
     * @param pageable configuration de la pagination et du tri
     * @return page de produits correspondants
     */
    Page<ProductResponse> findAll(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Remplace les données d'un produit existant.
     *
     * @param id identifiant du produit à modifier
     * @param request nouvelles données du produit
     * @return le produit modifié
     * @throws ProductNotFoundException si le produit n'existe pas
     * @throws DuplicateProductException si le nouveau SKU est déjà utilisé par un autre produit
     * @throws CategoryNotFoundException si la nouvelle catégorie n'existe pas
     */
    ProductResponse update(Long id, ProductRequest request);

    /**
     * Ajuste la quantité en stock d'un produit en entrée ou en sortie.
     *
     * @param id identifiant du produit
     * @param request valeur d'ajustement relative (positive ou négative)
     * @return le produit avec son stock actualisé
     * @throws ProductNotFoundException si le produit n'existe pas
     * @throws InsufficientStockException si une sortie dépasse le stock disponible
     */
    ProductResponse adjustStock(Long id, StockAdjustmentRequest request);

    /**
     * Supprime définitivement un produit existant.
     *
     * @param id identifiant du produit à supprimer
     * @throws ProductNotFoundException si le produit n'existe pas
     */
    void deleteById(Long id);
}
