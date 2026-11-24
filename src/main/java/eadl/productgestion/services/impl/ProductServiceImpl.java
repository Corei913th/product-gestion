package eadl.productgestion.services.impl;

import eadl.productgestion.dto.request.ProductRequestDTO;
import eadl.productgestion.dto.request.UpdateProductRequestDTO;
import eadl.productgestion.dto.response.ProductResponseDTO;
import eadl.productgestion.entity.Product;
import eadl.productgestion.exception.BusinessException;
import eadl.productgestion.exception.ResourceNotFoundException;
import eadl.productgestion.mappers.ProductMapper;
import eadl.productgestion.repositories.ProductRepository;
import eadl.productgestion.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Override
    public ProductResponseDTO save(ProductRequestDTO requestDTO) {
        Boolean productExists = productRepository.existsByCode(requestDTO.code());
        if(productExists) throw  new BusinessException("The product already exists ! ");

        return productMapper.toDTO(productRepository.save(productMapper.toEntity(requestDTO)));
    }

    @Override
    public ProductResponseDTO update(Long productId, UpdateProductRequestDTO productDTO) {
        Product product =  productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with " + productId + "id Not Found"));
        return null;
    }

    @Override
    public ProductResponseDTO findByCode(String code) {
        Product product =  productRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Product with " + code + "code Not Found"));
        return productMapper.toDTO(product);
    }

    @Override
    public List<ProductResponseDTO> findAllByNameContainingIgnoreCase(String name) {
        return productMapper.toDTOList(productRepository.findAllByToken(name));
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return productMapper.toDTOList(productRepository.findAll());
    }

    @Override
    public void deleteById(Long productId) {
        Product product =  productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with " + productId + "id Not Found"));
        productRepository.delete(product);
    }

    @Override
    public void deleteByCode(String code) {
        Product product =  productRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Product with " + code + "code Not Found"));
        productRepository.delete(product);
    }
}
