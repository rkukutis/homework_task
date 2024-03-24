package com.rhoopoe.myfashiontrunk.service;

import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Label;
import com.rhoopoe.myfashiontrunk.entity.Category;
import com.rhoopoe.myfashiontrunk.entity.CategoryAlias;
import com.rhoopoe.myfashiontrunk.entity.ItemImage;
import com.rhoopoe.myfashiontrunk.enumerated.CategoryType;
import com.rhoopoe.myfashiontrunk.enumerated.ImageItemIdentity;
import com.rhoopoe.myfashiontrunk.exception.FileTypeException;
import com.rhoopoe.myfashiontrunk.exception.ProhibitedItemException;
import com.rhoopoe.myfashiontrunk.exception.UnknownItemException;
import com.rhoopoe.myfashiontrunk.repository.CategoryRepository;
import com.rhoopoe.myfashiontrunk.repository.ImageRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ImageService.class})
@ActiveProfiles("test")
class ImageServiceTest {

    @Autowired
    private ImageService imageService;
    @MockBean
    private DetectionService mockDetectionService;
    @MockBean
    private ImageRepository mockImageRepository;
    @MockBean
    private CategoryRepository mockCategoryRepository;
    @MockBean
    private ImageUploadLogService mockLogService;
    @MockBean
    private S3Service mockS3Service;

    @Test
    public void givenReturnAllRequest_ThenReturnAllImageList() {
        ItemImage image1 = ItemImage.builder()
                .categories(new HashSet<>())
                .originalName("TEST1")
                .url("TEST_URL1")
                .build();

        ItemImage image2 = ItemImage.builder()
                .categories(new HashSet<>())
                .originalName("TEST2")
                .url("TEST_URL2")
                .build();

        Mockito.when(mockImageRepository.findAll()).thenReturn(List.of(image1, image2));
        List <ItemImage> returnedImages = imageService.getAllImages();
        assertEquals(2, returnedImages.size());
        assertEquals(image1, returnedImages.get(0));
        assertEquals(image2, returnedImages.get(1));

        DetectLabelsResult result = new DetectLabelsResult();
    }

    @Test
    public void givenNonImageFile_ThenThrowException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test.exe", "test.exe", "application/pdf", new byte[10]
        );
        assertThrows(FileTypeException.class, () -> imageService.createImage(mockMultipartFile));
    }

    @Test
    @SneakyThrows
    public void givenProhibitedImage_thenThrowException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test.jpg", "test.jpg", "image/jpg", new byte[10]
        );
        Category fakeCategory = Category.builder()
                .name("TEST_PROHIBITED")
                .type(CategoryType.PROHIBITED)
                .build();
        fakeCategory.addAlias(new CategoryAlias(fakeCategory, "weapon"));
        fakeCategory.addAlias(new CategoryAlias(fakeCategory, "gun"));
        Mockito.when(mockCategoryRepository.findByType(CategoryType.PROHIBITED)).thenReturn(List.of(fakeCategory));
        DetectLabelsResult fakeResult = new DetectLabelsResult();
        Label fakeLabel1 = new Label();
        fakeLabel1.setName("Gun");
        Label fakeLabel2 = new Label();
        fakeLabel2.setName("Rifle");
        fakeResult.setLabels(List.of(fakeLabel1, fakeLabel2));
        Mockito.when(mockDetectionService.getImageLabels(any())).thenReturn(fakeResult);
        assertThrows(ProhibitedItemException.class, () -> imageService.createImage(mockMultipartFile));

    }

    @Test
    @SneakyThrows
    public void givenUnknownImage_thenThrowException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test.jpg", "test.jpg", "image/jpg", new byte[10]
        );
        Category fakeProhibitedCategory = Category.builder()
                .name("TEST_PROHIBITED")
                .type(CategoryType.PROHIBITED)
                .build();
        fakeProhibitedCategory.addAlias(new CategoryAlias(fakeProhibitedCategory, "weapon"));
        fakeProhibitedCategory.addAlias(new CategoryAlias(fakeProhibitedCategory, "gun"));
        Mockito.when(mockCategoryRepository.findByType(CategoryType.PROHIBITED)).thenReturn(
                List.of(fakeProhibitedCategory)
        );

        Category fakeAllowedCategory = Category.builder()
                .name("TEST_ALLOWED")
                .type(CategoryType.ALLOWED)
                .build();
        fakeAllowedCategory.addAlias(new CategoryAlias(fakeAllowedCategory, "clothes"));
        fakeAllowedCategory.addAlias(new CategoryAlias(fakeAllowedCategory, "apparel"));
        Mockito.when(mockCategoryRepository.findByType(CategoryType.ALLOWED)).thenReturn(List.of(fakeAllowedCategory));

        DetectLabelsResult fakeResult = new DetectLabelsResult();
        Label fakeLabel1 = new Label();
        fakeLabel1.setName("Castle");
        Label fakeLabel2 = new Label();
        fakeLabel2.setName("Architecture");
        fakeResult.setLabels(List.of(fakeLabel1, fakeLabel2));
        Mockito.when(mockDetectionService.getImageLabels(any())).thenReturn(fakeResult);
        assertThrows(UnknownItemException.class, () -> imageService.createImage(mockMultipartFile));

    }

    @Test
    @SneakyThrows
    public void givenAllowedImage_thenReturnCreatedImage() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test.jpg", "test.jpg", "image/jpg", new byte[10]
        );
        Category fakeProhibitedCategory = Category.builder()
                .name("TEST_PROHIBITED")
                .type(CategoryType.PROHIBITED)
                .build();
        fakeProhibitedCategory.addAlias(new CategoryAlias(fakeProhibitedCategory, "weapon"));
        fakeProhibitedCategory.addAlias(new CategoryAlias(fakeProhibitedCategory, "gun"));
        Mockito.when(mockCategoryRepository.findByType(CategoryType.PROHIBITED)).thenReturn(
                List.of(fakeProhibitedCategory)
        );

        Category fakeAllowedCategory = Category.builder()
                .name("TEST_ALLOWED")
                .type(CategoryType.ALLOWED)
                .build();
        fakeAllowedCategory.addAlias(new CategoryAlias(fakeAllowedCategory, "clothes"));
        fakeAllowedCategory.addAlias(new CategoryAlias(fakeAllowedCategory, "apparel"));
        Mockito.when(mockCategoryRepository.findByType(CategoryType.ALLOWED)).thenReturn(List.of(fakeAllowedCategory));

        DetectLabelsResult fakeResult = new DetectLabelsResult();
        Label fakeLabel1 = new Label();
        fakeLabel1.setName("Clothes");
        Label fakeLabel2 = new Label();
        fakeLabel2.setName("Shirt");
        fakeResult.setLabels(List.of(fakeLabel1, fakeLabel2));

        Mockito.when(mockS3Service.uploadImage(any(File.class), anyString(), anyString())).thenReturn("TEST_URL");
        Mockito.when(mockDetectionService.getImageLabels(any())).thenReturn(fakeResult);

        imageService.createImage(mockMultipartFile);
        Mockito.verify(mockCategoryRepository, Mockito.times(2))
                .findByType(any(CategoryType.class));
        ArgumentCaptor<Set<Category>> categoryCaptor = ArgumentCaptor.forClass(Set.class);
        Mockito.verify(mockLogService, Mockito.times(1)).createLog(
                eq("test.jpg"), eq(mockMultipartFile.getBytes()), eq(ImageItemIdentity.ALLOWED), categoryCaptor.capture()
        );
        assertEquals(Set.of(fakeAllowedCategory), categoryCaptor.getValue());
        ArgumentCaptor<ItemImage> itemImageCaptor = ArgumentCaptor.forClass(ItemImage.class);
        Mockito.verify(mockImageRepository, Mockito.times(1)).save(itemImageCaptor.capture());
        assertEquals(Set.of(fakeAllowedCategory), itemImageCaptor.getValue().getCategories());
        assertEquals("test.jpg", itemImageCaptor.getValue().getOriginalName());
        assertEquals("TEST_URL", itemImageCaptor.getValue().getUrl());
    }

}