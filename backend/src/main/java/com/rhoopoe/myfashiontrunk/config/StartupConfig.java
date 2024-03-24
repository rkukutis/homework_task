package com.rhoopoe.myfashiontrunk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.rhoopoe.myfashiontrunk.entity.Category;
import com.rhoopoe.myfashiontrunk.entity.CategoryAlias;
import com.rhoopoe.myfashiontrunk.dto.CategoryDTO;
import com.rhoopoe.myfashiontrunk.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class StartupConfig {
    private final CategoryRepository categoryRepository;

    private InputStream getFileAsIOStream(String fileName) {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }

    @Bean
    CommandLineRunner initialize() {
        return args -> {
            InputStream is = getFileAsIOStream("categories.json");
            StringBuilder jsonBuilder = new StringBuilder();
            try (Scanner scanner = new Scanner(is)) {
                while (scanner.hasNextLine()) {
                    jsonBuilder.append(scanner.nextLine());
                }
            }
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<CategoryDTO> categoryDTOList = objectMapper.readValue(
                    jsonBuilder.toString(),
                    typeFactory.constructCollectionType(List.class, CategoryDTO.class));
            for (CategoryDTO categoryDTO : categoryDTOList) {
                if (!categoryRepository.existsByNameIgnoreCase(categoryDTO.getName())) {
                    Category newCategory = Category.builder()
                            .name(categoryDTO.getName())
                            .type(categoryDTO.getType())
                            .build();
                    Category savedCategory = categoryRepository.save(newCategory);
                    for (String aliasString : categoryDTO.getAliases()) {
                        CategoryAlias newAlias = new CategoryAlias(savedCategory, aliasString);
                        savedCategory.addAlias(newAlias);
                    }
                    categoryRepository.save(savedCategory);
                }
            }
        };
    }
}
