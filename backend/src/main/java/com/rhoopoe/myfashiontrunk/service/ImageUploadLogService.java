package com.rhoopoe.myfashiontrunk.service;

import com.rhoopoe.myfashiontrunk.entity.Category;
import com.rhoopoe.myfashiontrunk.enumerated.ImageItemIdentity;
import com.rhoopoe.myfashiontrunk.entity.ImageUploadLog;
import com.rhoopoe.myfashiontrunk.repository.CategoryRepository;
import com.rhoopoe.myfashiontrunk.repository.ImageUploadLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// The normal image recognition model isn't very accurate, so the main idea behind this log service was
// that moderator intervention would contribute to the accuracy of image recognition

// Moderators would receive reports of incorrectly validated images and their feedback would be fed back
// to a classifier model that is using custom labels.

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ImageUploadLogService {
    private final ImageUploadLogRepository logRepository;
    private final CategoryRepository categoryRepository;

    public List<ImageUploadLog> getAllLogs() {
        return logRepository.findAll();
    }

    public void createLog(String originalName, byte[] fileBytes, ImageItemIdentity identifiedAs,
                          Set<Category> identifiedCategories) {
        if (originalName == null || fileBytes == null || identifiedAs == null) {
            throw new IllegalArgumentException("All arguments must not be null");
        }
        if (originalName.isBlank()) {
            throw new IllegalArgumentException("Original file name must not be blank");
        }
        if (fileBytes.length == 0) {
            throw  new IllegalArgumentException("File byte array length must be positive");
        }
        String checksum;
        try {
            // a checksum would be useful for preventing the upload of files that were marked PROHIBITED
            byte[] hash = MessageDigest.getInstance("MD5").digest(fileBytes);
            checksum = new BigInteger(1, hash).toString(16);
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException("Could not get MD5 hashing algorithm");
        }
        ImageUploadLog newLog = ImageUploadLog.builder()
                .originalFileName(originalName)
                .identifiedAs(identifiedAs)
                .identifiedCategories(identifiedCategories)
                .md5Checksum(checksum)
                .build();
        ImageUploadLog saveLog = logRepository.save(newLog);
        identifiedCategories.forEach(category -> category.addLog(saveLog));
        categoryRepository.saveAll(identifiedCategories);

        log.info(
                "Created log entity for file {}, identity={}, categories={}",
                originalName, identifiedAs, identifiedCategories.stream().map(Category::getName).toList()
        );
    }
    public void createLog(String originalName, byte[] fileBytes, ImageItemIdentity identifiedAs) {
        this.createLog(originalName, fileBytes, identifiedAs, new HashSet<>());
    }
}
