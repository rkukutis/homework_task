package com.rhoopoe.myfashiontrunk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhoopoe.myfashiontrunk.entity.Category;
import com.rhoopoe.myfashiontrunk.entity.CategoryAlias;
import com.rhoopoe.myfashiontrunk.enumerated.CategoryType;
import com.rhoopoe.myfashiontrunk.model.CategoryDTO;
import com.rhoopoe.myfashiontrunk.service.CategoryService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CategoryService mockCategoryService;

    @Test
    void givenDefaultPagination_thenReturnDefaultPage() throws Exception {
        Category fakeCategory = Category.builder()
                .name("Nice things")
                .type(CategoryType.ALLOWED)
                .build();
        fakeCategory.addAlias(new CategoryAlias(fakeCategory, "test-alias"));

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "createdAt");
        Pageable pageSettings = PageRequest.of(0, 25, sort);
        Page<Category> fakePage = new PageImpl<>(List.of(fakeCategory), pageSettings, 1);
        Mockito.when(mockCategoryService.getCategoryPage(any(Pageable.class))).thenReturn(fakePage);

        ObjectMapper mapper = new ObjectMapper();
        String expectedJSON = mapper.writeValueAsString(fakePage);
        ArgumentCaptor<PageRequest> paginationCaptor = ArgumentCaptor.forClass(PageRequest.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andExpect(content().json(expectedJSON));

        verify(mockCategoryService, Mockito.times(1))
                .getCategoryPage(paginationCaptor.capture());

        assertEquals(0, paginationCaptor.getValue().getPageNumber());
        assertEquals(25, paginationCaptor.getValue().getPageSize());
        List<String> sortByList = paginationCaptor.getValue().getSort()
                .get().map(Sort.Order::getProperty).toList();
        List<Sort.Direction> sortDirectionList = paginationCaptor.getValue().getSort()
                .get().map(Sort.Order::getDirection).toList();
        assertEquals("createdAt", sortByList.get(0));
        assertEquals(Sort.Direction.ASC, sortDirectionList.get(0));
    }

    @Test
    void givenValidCategory_thenCallServiceCreationMethod() throws Exception {
        CategoryDTO fakeCategoryDTO = new CategoryDTO("category", CategoryType.ALLOWED, List.of("alias"));
        ObjectMapper mapper = new ObjectMapper();
        String inputJSON = mapper.writeValueAsString(fakeCategoryDTO);

        Category fakeCategory = Category.builder()
                .name("Nice things")
                .type(CategoryType.ALLOWED)
                .build();
        fakeCategory.addAlias(new CategoryAlias(fakeCategory, "test-alias"));
        Mockito.when(mockCategoryService.createCategory(any(CategoryDTO.class))).thenReturn(fakeCategory);
        String outputJSON = mapper.writeValueAsString(fakeCategory);


        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(inputJSON)
        ).andExpect(status().isOk()).andExpect(content().json(outputJSON));

        ArgumentCaptor<CategoryDTO> categoryDTOArgumentCaptor = ArgumentCaptor.forClass(CategoryDTO.class);
        verify(mockCategoryService, times(1)).createCategory(categoryDTOArgumentCaptor.capture());
        assertEquals(fakeCategoryDTO.getName(), categoryDTOArgumentCaptor.getValue().getName());
        assertEquals(fakeCategoryDTO.getType(), categoryDTOArgumentCaptor.getValue().getType());
        assertEquals(fakeCategoryDTO.getAliases(), categoryDTOArgumentCaptor.getValue().getAliases());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    ", "\n"})
    void givenInvalidName_thenReturn400(String name) throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO(name, CategoryType.ALLOWED, List.of("alias"));
        ObjectMapper mapper = new ObjectMapper();
        String inputJSON = mapper.writeValueAsString(categoryDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(inputJSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void givenNoAliases_thenReturn400() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO("test", CategoryType.ALLOWED, new ArrayList<>());
        ObjectMapper mapper = new ObjectMapper();
        String inputJSON = mapper.writeValueAsString(categoryDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(inputJSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidType_thenReturn400() throws Exception {

        JSONObject json = new JSONObject();
        json.put("name", "test");
        json.put("type", "INVALID");
        JSONArray array = new JSONArray();
        array.put("alias");
        json.put("aliases", array);

        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json.toString())
        ).andExpect(status().isBadRequest());
    }

    @Test
    void givenVariedCaseType_thenReturnOk() throws Exception {

        JSONObject json = new JSONObject();
        json.put("name", "test");
        json.put("type", "AlLoWeD");
        JSONArray array = new JSONArray();
        array.put("alias");
        json.put("aliases", array);

        mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json.toString())
        ).andExpect(status().isOk());
    }

    @Test
    void givenUUID_thenCallDelete() throws Exception {
        UUID randomUUID = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/" + randomUUID)
        ).andExpect(status().isNoContent());

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockCategoryService, times(1)).deleteCategory(idCaptor.capture());
        assertEquals(randomUUID.toString(), idCaptor.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sadfasdfassd", "    ", "\n", "158bc-9d92-405f-a28-e12276d9847"})
    void givenInvalidUUID_thenReturn400(String uuidString) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/" + uuidString)
        ).andExpect(status().isBadRequest());
    }
}