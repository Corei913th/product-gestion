package eadl.productgestion.services;

import eadl.productgestion.dto.request.CategoryRequest;
import eadl.productgestion.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    List<CategoryResponse> findAll();

    CategoryResponse findById(Long id);
}
