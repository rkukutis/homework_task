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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CategoryService.class})
@ActiveProfiles("test")
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository mockCategoryRepository;

    @MockBean
    private ImageRepository mockImageRepository;

    @MockBean
    private ImageUploadLogRepository mockLogRepository;


    @Test
    void givenNullArgument_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(null));
    }

    @Test
    void givenNewCategoryDTO_thenCreateCategory() {
        CategoryDTO categoryDTO = new CategoryDTO("test", CategoryType.ALLOWED, List.of("alias1", "alias2"));
        Category fakeCategory = Category.builder()
                .name("test")
                .type(CategoryType.ALLOWED)
                .build();
        when(mockCategoryRepository.save(any(Category.class))).thenReturn(fakeCategory);
        categoryService.createCategory(categoryDTO);
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(mockCategoryRepository, times(2)).save(categoryCaptor.capture());
        assertEquals(fakeCategory, categoryCaptor.getValue());
        assertEquals(2, categoryCaptor.getValue().getAliases().size());
    }

    @Test
    void givenAlreadyExistingCategory_thenThrowException() {
        CategoryDTO categoryDTO = new CategoryDTO("test", CategoryType.ALLOWED, List.of("alias1", "alias2"));
        when(mockCategoryRepository.existsByNameIgnoreCase("test")).thenReturn(true);
        assertThrows(EntityExistsException.class, () -> categoryService.createCategory(categoryDTO));
    }

    @Test
    void givenNullOrEmptyIdToDelete_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(null));
        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(""));
    }

    @Test
    void givenDeleteRequest_whenImagesAndLogHasNoOtherCategories_thenRemoveOnlyCategory() {
        UUID categoryId = UUID.randomUUID();
        Category fakeCategory = Category.builder()
                .id(categoryId)
                .name("test")
                .type(CategoryType.ALLOWED)
                .build();

        // set of categories is empty if the deleted category is the last one

        UUID imageId = UUID.randomUUID();
        ItemImage fakeImage = ItemImage.builder()
                .id(imageId)
                .url("url")
                .categories(new HashSet<>())
                .originalName("test")
                .build();

        UUID logId = UUID.randomUUID();
        ImageUploadLog fakeLog = ImageUploadLog.builder()
                .id(logId)
                .md5Checksum("testSum")
                .identifiedCategories(new HashSet<>())
                .originalFileName("test")
                .build();

        fakeCategory.setItemImages(Set.of(fakeImage));
        fakeCategory.setImageUploadLogs(Set.of(fakeLog));

        when(mockCategoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(fakeCategory));

        categoryService.deleteCategory(categoryId.toString());
        ArgumentCaptor<UUID> logIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(mockImageRepository, times(1)).deleteById(logIdCaptor.capture());
        ArgumentCaptor<UUID> imageIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(mockImageRepository, times(1)).deleteById(imageIdCaptor.capture());
    }

}