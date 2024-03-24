package com.rhoopoe.myfashiontrunk.service;

import com.rhoopoe.myfashiontrunk.entity.Category;
import com.rhoopoe.myfashiontrunk.entity.CategoryAlias;
import com.rhoopoe.myfashiontrunk.entity.ImageUploadLog;
import com.rhoopoe.myfashiontrunk.entity.ItemImage;
import com.rhoopoe.myfashiontrunk.enumerated.CategoryType;
import com.rhoopoe.myfashiontrunk.model.CategoryDTO;
import com.rhoopoe.myfashiontrunk.repository.CategoryRepository;
import com.rhoopoe.myfashiontrunk.repository.ImageRepository;
import com.rhoopoe.myfashiontrunk.repository.ImageUploadLogRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ImageUploadLogRepository logRepository;
    public Page<Category> getCategoryPage(Pageable pageSettings) {
        return categoryRepository.findAll(pageSettings);
    }

    public Category createCategory(CategoryDTO categoryRequest) {
        if (categoryRequest == null) {
            throw new IllegalArgumentException("category DTO must not be null");
        }
        if (categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())) {
            throw new EntityExistsException("Category with name " + categoryRequest.getName() + " already exists");
        }

        Category newCategory = Category.builder()
                .name(categoryRequest.getName())
                .type(categoryRequest.getType())
                .build();
        Category savedCategory = categoryRepository.save(newCategory);

        for (String alias : categoryRequest.getAliases()) {
            CategoryAlias newAlias = new CategoryAlias(savedCategory, alias);
            log.info("Added CategoryAlias {} for category {}", alias, savedCategory.getName());
            savedCategory.addAlias(newAlias);
        }
        return categoryRepository.save(savedCategory);
    }

    public void deleteCategory(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            throw new IllegalArgumentException("category Id must not be null or blank");
        }
        UUID id = UUID.fromString(categoryId);
        Category categoryToBeDeleted = categoryRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Category " + id + " not found"));
        Set<ImageUploadLog> associatedLogs = categoryToBeDeleted.getImageUploadLogs();
        Set<ItemImage> associatedImages = categoryToBeDeleted.getItemImages();
        categoryRepository.deleteById(id);

        // remove image or log if it loses all categories
        // doing this manually due to lack of orphan removal for @ManyToMany
        for (ImageUploadLog uploadLog : associatedLogs) {
            if (uploadLog.getIdentifiedCategories().isEmpty()) {
                logRepository.deleteById(uploadLog.getId());
            }
        }
        for (ItemImage itemImage : associatedImages) {
            if (itemImage.getCategories().isEmpty()) {
                imageRepository.deleteById(itemImage.getId());
            }
        }
        log.info("Deleted category with id {}", id);
    }
}
