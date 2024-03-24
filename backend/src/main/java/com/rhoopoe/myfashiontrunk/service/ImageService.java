package com.rhoopoe.myfashiontrunk.service;

import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Label;
import com.rhoopoe.myfashiontrunk.entity.*;
import com.rhoopoe.myfashiontrunk.enumerated.CategoryType;
import com.rhoopoe.myfashiontrunk.enumerated.ImageItemIdentity;
import com.rhoopoe.myfashiontrunk.exception.FileTypeException;
import com.rhoopoe.myfashiontrunk.exception.ProhibitedItemException;
import com.rhoopoe.myfashiontrunk.exception.UnknownItemException;
import com.rhoopoe.myfashiontrunk.repository.CategoryRepository;
import com.rhoopoe.myfashiontrunk.repository.ImageRepository;
import com.rhoopoe.myfashiontrunk.util.FileUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    // using interfaces here instead of implementations would make this more extendable and modular
    // but due to the limited scope of this task I opted for the simpler approach of using the implementations directly
    private final S3Service s3Service;
    private final DetectionService detectionService;
    private final ImageUploadLogService logService;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;

    private String trimLowerCaseString(String string) {
        return string.trim().toLowerCase(Locale.ROOT);
    }

    // Method checks if any of the detection labels match an alias of a category
    // Accuracy can be increased by supplying more aliases for each category
    // Another way to increase accuracy may be to also check the label's parents and categories
    // This method would require a matched word threshold which should be determined experimentally
    private Set<Category> getImageCategories(DetectLabelsResult detectionResult, CategoryType accessMatch) {
        List<Category> storedCategories = categoryRepository.findByType(accessMatch);
        Set<Category> matchedCategories = new HashSet<>();
        for (Category category : storedCategories) {
            for (CategoryAlias categoryAlias : category.getAliases()) {
                for (Label detectionLabel : detectionResult.getLabels()) {
                    boolean matchesLabelName = trimLowerCaseString(detectionLabel.getName())
                            .equalsIgnoreCase(categoryAlias.getAliasString());
                    if (matchesLabelName) {
                        log.info(
                                "Rekognition label {} matched category alias {}",
                                detectionLabel.getName(), categoryAlias.getAliasString()
                        );
                        matchedCategories.add(category);
                    }
                }
            }
        }
        log.info(
                "Image matched {} categories {}",
                accessMatch.toString(), matchedCategories.stream().map(Category::getName).toList()
        );
        return matchedCategories;
    }

    // Not using @Transactional here because doing so would lock up DB resources
    public ItemImage createImage(@NonNull MultipartFile file) {
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/jp")) {
            throw new FileTypeException("Only image files must be provided");
        }
        String extension = "." + contentType.split("/")[1];

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException exception) {
            throw new RuntimeException("Could not get image bytes");
        }

        // First we check if any labels match any category aliases
        DetectLabelsResult detectionResult = detectionService.getImageLabels(fileBytes);
        Set<Category> matchedProhibitedCategories = getImageCategories(detectionResult, CategoryType.PROHIBITED);

        if (!matchedProhibitedCategories.isEmpty()) {
            logService.createLog(
                    file.getOriginalFilename(), fileBytes, ImageItemIdentity.PROHIBITED, matchedProhibitedCategories
            );
            throw new ProhibitedItemException("Image is of a prohibited item", matchedProhibitedCategories);
        }
        // then we check the image matches on of the approved categories
        // An image is rejected if it does not match any category, which is the more cautious approach
        Set<Category> matchedAllowedCategories = getImageCategories(detectionResult, CategoryType.ALLOWED);
        if (matchedAllowedCategories.isEmpty()) {
            logService.createLog(file.getOriginalFilename(), fileBytes, ImageItemIdentity.UNKNOWN);
            throw new UnknownItemException("Image is of an item that is not on allowed list");
        }
        logService.createLog(
                file.getOriginalFilename(), fileBytes, ImageItemIdentity.ALLOWED, matchedAllowedCategories
        );

        File imageFile = FileUtil.convertByteArrayToFile(fileBytes);
        // approved images are uploaded to S3 storage and saved in the database
        UUID newImageId = UUID.randomUUID();
        ItemImage itemImage = ItemImage.builder()
                .id(newImageId)
                .categories(matchedAllowedCategories)
                .originalName(file.getOriginalFilename())
                .url(s3Service.uploadImage(imageFile, newImageId + extension, contentType))
                .build();

        ItemImage createdImage = imageRepository.save(itemImage);
        matchedAllowedCategories.forEach(category -> category.addImage(createdImage));
        categoryRepository.saveAll(matchedAllowedCategories);
        return createdImage;
    }

    public List<ItemImage> getAllImages() {
        return imageRepository.findAll();
    }
}
