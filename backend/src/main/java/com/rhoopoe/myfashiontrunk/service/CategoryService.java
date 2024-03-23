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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ImageUploadLogRepository logRepository;
    public Page<Category> getCategoryPage(Pageable pageSettings) {
        return categoryRepository.findAll(pageSettings);
    }

    public Category createCategory(CategoryDTO categoryRequest) {
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

    public void deleteCategory(UUID categoryId) {
        Category categoryToBeDeleted = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException("Category " + categoryId + " not found"));
        Set<ImageUploadLog> associatedLogs = categoryToBeDeleted.getImageUploadLogs();
        Set<ItemImage> associatedImages = categoryToBeDeleted.getItemImages();
        categoryRepository.deleteById(categoryId);

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
        log.info("Deleted category with id {}", categoryId);
    }
}
