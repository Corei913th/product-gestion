package eadl.productgestion.services;

import eadl.productgestion.dto.request.ProductRequest;
import eadl.productgestion.dto.request.StockAdjustmentRequest;
import eadl.productgestion.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    ProductResponse findById(Long id);

    Page<ProductResponse> findAll(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse adjustStock(Long id, StockAdjustmentRequest request);

    void deleteById(Long id);
}
