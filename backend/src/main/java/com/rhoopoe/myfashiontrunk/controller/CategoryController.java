package com.rhoopoe.myfashiontrunk.controller;

import com.rhoopoe.myfashiontrunk.entity.Category;
import com.rhoopoe.myfashiontrunk.model.CategoryDTO;
import com.rhoopoe.myfashiontrunk.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin
public class
CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<Category>> getCategories(@RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(name = "limit", defaultValue = "50") int limit,
                                           @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                           @RequestParam(name = "sortDesc", defaultValue = "false") boolean sortDesc)
    {
        Sort.Direction direction = sortDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageSettings = PageRequest.of(page, limit, sort);
        log.info(
                "Received GET request for category page with parameters: page={}, limit={}, sortBy={}, sortDesc={}",
                page, limit, sortBy, sortDesc
        );
        Page<Category> categoryPage = categoryService.getCategoryPage(pageSettings);
        log.info(
                "Returning category page with {} results", categoryPage.getNumberOfElements()
        );
        return ResponseEntity.ok().body(categoryPage);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody @Valid CategoryDTO categoryRequest) {
        log.info(
                "Received POST request for category with name={}, type={}, aliases={}",
                categoryRequest.getName(), categoryRequest.getType(), categoryRequest.getAliases()
        );
        Category createdCategory = categoryService.createCategory(categoryRequest);
        log.info(
                "Returning created category {}", createdCategory
        );
        return ResponseEntity.ok().body(createdCategory);
    }

    @DeleteMapping("{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
