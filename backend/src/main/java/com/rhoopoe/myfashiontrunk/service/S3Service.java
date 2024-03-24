package com.rhoopoe.myfashiontrunk.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
@Slf4j
public class S3Service {

    @Value("${app.image-bucket}")
    private String imageBucket;

    public String uploadImage(@NonNull File imageFile,@NotNull String fileName,@NonNull String contentType) {
        if (fileName.isBlank() || contentType.isBlank()) {
            throw new IllegalArgumentException("String arguments must not be blank");
        }
        AmazonS3Client s3Client = (AmazonS3Client) AmazonS3ClientBuilder.defaultClient();
        PutObjectRequest request = new PutObjectRequest(imageBucket, fileName, imageFile)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        request.setMetadata(metadata);
        s3Client.putObject(request);
        log.info("Uploaded image {} to bucket {}", fileName, imageBucket);
        String uploadedObjectURL = s3Client.getResourceUrl(imageBucket, fileName);
        log.info("Returning uploaded object public URL {}", uploadedObjectURL);
        return uploadedObjectURL;
    }
}

