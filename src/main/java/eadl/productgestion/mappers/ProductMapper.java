package eadl.productgestion.mappers;


import eadl.productgestion.dto.request.ProductRequestDTO;
import eadl.productgestion.dto.response.ProductResponseDTO;
import eadl.productgestion.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    Product toEntity(ProductRequestDTO productRequestDTO);

    ProductResponseDTO  toDTO(Product product);

    List<ProductResponseDTO> toDTOList(List<Product> products);

    List<Product> toEntityList(List<ProductRequestDTO> productRequestDTOS);
}
