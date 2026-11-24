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
import eadl.productgestion.repositories.ProductSpecification;
import eadl.productgestion.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
        checkSkuUniqueness(request.sku(), null);
        Category category = getCategoryOrThrow(request.categoryId());

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        log.info("Produit créé: id={}, sku={}", saved.getId(), saved.getSku());
        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse findById(Long id) {
        return productMapper.toResponse(getProductOrThrow(id));
    }

    @Override
    public Page<ProductResponse> findAll(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterBy(name, categoryId, minPrice, maxPrice);
        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProductOrThrow(id);
        checkSkuUniqueness(request.sku(), id);
        Category category = getCategoryOrThrow(request.categoryId());

        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setCategory(category);

        Product updated = productRepository.save(product);
        log.info("Produit mis à jour: id={}, sku={}", updated.getId(), updated.getSku());
        return productMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ProductResponse adjustStock(Long id, StockAdjustmentRequest request) {
        Product product = getProductOrThrow(id);
        int newQuantity = product.getQuantity() + request.delta();

        if (newQuantity < 0) {
            log.warn("Stock insuffisant pour id={}: dispo={}, demandé={}", id, product.getQuantity(), Math.abs(request.delta()));
            throw new InsufficientStockException(id, Math.abs(request.delta()), product.getQuantity());
        }

        product.setQuantity(newQuantity);
        Product saved = productRepository.save(product);
        log.info("Stock ajusté pour id={}: nouveau stock={}", id, newQuantity);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productRepository.delete(getProductOrThrow(id));
        log.info("Produit supprimé: id={}", id);
    }

    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    private void checkSkuUniqueness(String sku, Long currentId) {
        boolean exists = (currentId == null)
                ? productRepository.existsBySku(sku)
                : productRepository.existsBySkuAndIdNot(sku, currentId);
        if (exists) {
            log.warn("SKU déjà utilisé: {}", sku);
            throw new DuplicateProductException(sku);
        }
    }
}
