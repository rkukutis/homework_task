package com.rhoopoe.myfashiontrunk.controller;

import com.rhoopoe.myfashiontrunk.entity.ItemImage;
import com.rhoopoe.myfashiontrunk.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;

    @PostMapping(headers = "content-type=multipart/form-data", consumes = "image/jpg")
    public ResponseEntity<ItemImage> uploadImage(@RequestParam("image")MultipartFile file) throws IOException {
        log.info("Received POST multipart request with file {}", file.getOriginalFilename());
        ItemImage uploadedImage = imageService.createImage(file);
        log.info("Returning approved image {} entity", uploadedImage.getOriginalName());
        return ResponseEntity.ok().body(uploadedImage);
    }

    @GetMapping
    public ResponseEntity<List<ItemImage>> getAllImages() {
        // pagination would be best here, but I chose the easier method
        List<ItemImage> images = imageService.getAllImages();
        Collections.reverse(images);
        return ResponseEntity.ok().body(images);
    }
}
