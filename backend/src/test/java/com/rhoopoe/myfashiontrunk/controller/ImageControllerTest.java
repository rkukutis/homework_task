package com.rhoopoe.myfashiontrunk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhoopoe.myfashiontrunk.entity.Category;
import com.rhoopoe.myfashiontrunk.entity.CategoryAlias;
import com.rhoopoe.myfashiontrunk.entity.ItemImage;
import com.rhoopoe.myfashiontrunk.enumerated.CategoryType;
import com.rhoopoe.myfashiontrunk.service.ImageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@ActiveProfiles("test")
class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ImageService mockImageService;

    @Test
    void givenMultipartFile_thenReturnCreatedEntity() throws Exception {
        MockMultipartFile fakeMultipart = new MockMultipartFile(
                "test.jpg", "test.jpg","image/jpeg", new byte[10]
        );
        Category fakeCategory = Category.builder()
                .name("Nice things")
                .type(CategoryType.ALLOWED)
                .build();
        fakeCategory.addAlias(new CategoryAlias(fakeCategory, "test-alias"));

        ItemImage fakeItemImage = ItemImage.builder()
                .originalName("TEST")
                .categories(Set.of(fakeCategory))
                .url("url")
                .build();

        Mockito.when(mockImageService.createImage(any(MultipartFile.class))).thenReturn(fakeItemImage);

        ObjectMapper mapper = new ObjectMapper();
        String expectedJSON = mapper.writeValueAsString(fakeItemImage);

        mockMvc.perform(multipart("/images")
                .file("image", fakeMultipart.getBytes())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andExpect(content().json(expectedJSON));
    }

    @Test
    void givenNoMultipartFile_thenReturn400() throws Exception {
        MockMultipartFile fakeMultipart = new MockMultipartFile(
                "test.jpg", "test.jpg","image/jpeg", new byte[10]
        );
        mockMvc.perform(multipart("/images").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAllImageRequest_thenReturnAllImage() throws Exception {
        Category fakeCategory = Category.builder()
                .name("TEST")
                .type(CategoryType.ALLOWED)
                .build();
        fakeCategory.addAlias(new CategoryAlias(fakeCategory, "test"));

        ItemImage fakeItemImage = ItemImage.builder()
                .originalName("name")
                .categories(Set.of(fakeCategory))
                .url("url")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String expectedJson = mapper.writeValueAsString(List.of(fakeItemImage));

        Mockito.when(mockImageService.getAllImages()).thenReturn(List.of(fakeItemImage));

        mockMvc.perform(get("/images").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().json(expectedJson));
    }
}