package eadl.productgestion.services;

import eadl.productgestion.dto.request.ProductRequest;
import eadl.productgestion.dto.request.StockAdjustmentRequest;
import eadl.productgestion.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Contrat de service pour la gestion des produits et du stock.
 */
public interface ProductService {

    /**
     * Crée un nouveau produit.
     *
     * @param request données du produit à créer
     * @return le produit enregistré
     */
    ProductResponse create(ProductRequest request);

    /**
     * Recherche un produit par son identifiant.
     *
     * @param id identifiant du produit
     * @return le produit correspondant
     */
    ProductResponse findById(Long id);

    /**
     * Recherche des produits selon des critères avec pagination et tri.
     *
     * @param name filtre textuel sur le nom
     * @param categoryId identifiant de la catégorie
     * @param minPrice prix minimum
     * @param maxPrice prix maximum
     * @param pageable configuration de pagination et de tri
     * @return une page de produits
     */
    Page<ProductResponse> findAll(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Modifie les informations d'un produit existant.
     *
     * @param id identifiant du produit à modifier
     * @param request nouvelles données du produit
     * @return le produit mis à jour
     */
    ProductResponse update(Long id, ProductRequest request);

    /**
     * Ajuste la quantité en stock d'un produit.
     *
     * @param id identifiant du produit
     * @param request variation de stock positive ou négative
     * @return le produit avec son stock à jour
     */
    ProductResponse adjustStock(Long id, StockAdjustmentRequest request);

    /**
     * Supprime un produit par son identifiant.
     *
     * @param id identifiant du produit à supprimer
     */
    void deleteById(Long id);
}
