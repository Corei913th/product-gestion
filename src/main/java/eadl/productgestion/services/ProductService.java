package eadl.productgestion.services;

import eadl.productgestion.dto.request.ProductRequestDTO;
import eadl.productgestion.dto.request.UpdateProductRequestDTO;
import eadl.productgestion.dto.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO save(ProductRequestDTO requestDTO);

    ProductResponseDTO update(Long productId, UpdateProductRequestDTO requestDTO);

    ProductResponseDTO findByCode(String code);

    List<ProductResponseDTO> findAllByNameContainingIgnoreCase(String name);

    List<ProductResponseDTO> findAll();

    void deleteById(Long productId);

    void deleteByCode(String code);


}
