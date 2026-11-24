package eadl.productgestion.services.impl;

import eadl.productgestion.dto.request.CategoryRequest;
import eadl.productgestion.dto.response.CategoryResponse;
import eadl.productgestion.entity.Category;
import eadl.productgestion.exception.CategoryNotFoundException;
import eadl.productgestion.exception.DuplicateCategoryException;
import eadl.productgestion.mappers.CategoryMapper;
import eadl.productgestion.repositories.CategoryRepository;
import eadl.productgestion.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String trimmedName = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            log.warn("Tentative de création d'une catégorie déjà existante: {}", trimmedName);
            throw new DuplicateCategoryException(trimmedName);
        }

        Category category = new Category(trimmedName);
        Category savedCategory = categoryRepository.save(category);
        log.info("Catégorie créée avec succès: id={}, nom={}", savedCategory.getId(), savedCategory.getName());
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }

    @Override
    public CategoryResponse findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Catégorie introuvable avec l'id: {}", id);
                    return new CategoryNotFoundException(id);
                });
    }
}
