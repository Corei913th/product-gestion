package eadl.productgestion.services.impl;

import eadl.productgestion.dto.request.ProductRequest;
import eadl.productgestion.dto.request.StockAdjustmentRequest;
import eadl.productgestion.dto.response.ProductResponse;
import eadl.productgestion.entity.Category;
import eadl.productgestion.entity.Product;
import eadl.productgestion.exception.CategoryNotFoundException;
import eadl.productgestion.exception.DuplicateProductException;
import eadl.productgestion.exception.InsufficientStockException;
import eadl.productgestion.exception.ProductNotFoundException;
import eadl.productgestion.mappers.ProductMapper;
import eadl.productgestion.repositories.CategoryRepository;
import eadl.productgestion.repositories.ProductRepository;
import eadl.productgestion.services.ProductService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            log.warn("Création refusée: SKU déjà existant: {}", request.sku());
            throw new DuplicateProductException(request.sku());
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> {
                    log.warn("Création refusée: catégorie introuvable id={}", request.categoryId());
                    return new CategoryNotFoundException(request.categoryId());
                });

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        log.info("Produit créé avec succès: id={}, sku={}", savedProduct.getId(), savedProduct.getSku());
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Produit introuvable: id={}", id);
                    return new ProductNotFoundException(id);
                });
    }

    @Override
    public Page<ProductResponse> findAll(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Modification refusée: produit introuvable id={}", id);
                    return new ProductNotFoundException(id);
                });

        if (productRepository.existsBySkuAndIdNot(request.sku(), id)) {
            log.warn("Modification refusée: SKU déjà utilisé par un autre produit: {}", request.sku());
            throw new DuplicateProductException(request.sku());
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> {
                    log.warn("Modification refusée: catégorie introuvable id={}", request.categoryId());
                    return new CategoryNotFoundException(request.categoryId());
                });

        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        log.info("Produit mis à jour: id={}, sku={}", updatedProduct.getId(), updatedProduct.getSku());
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponse adjustStock(Long id, StockAdjustmentRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ajustement de stock refusé: produit introuvable id={}", id);
                    return new ProductNotFoundException(id);
                });

        int delta = request.delta();
        int newQuantity = product.getQuantity() + delta;

        if (newQuantity < 0) {
            log.warn("Stock insuffisant pour id={}: disponible={}, demandé={}", id, product.getQuantity(), Math.abs(delta));
            throw new InsufficientStockException(id, Math.abs(delta), product.getQuantity());
        }

        product.setQuantity(newQuantity);
        Product savedProduct = productRepository.save(product);
        log.info("Stock ajusté pour id={}: nouveau stock={}", id, newQuantity);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Suppression refusée: produit introuvable id={}", id);
                    return new ProductNotFoundException(id);
                });

        productRepository.delete(product);
        log.info("Produit supprimé avec succès: id={}", id);
    }
}
