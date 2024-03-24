package com.rhoopoe.myfashiontrunk.controller;

import com.rhoopoe.myfashiontrunk.entity.ImageUploadLog;
import com.rhoopoe.myfashiontrunk.service.ImageUploadLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("logs")
@Slf4j
@RequiredArgsConstructor
public class LogController {
    private final ImageUploadLogService logService;

    @GetMapping
    public List<ImageUploadLog> getAllUploadLogs() {
        // pagination would be best here, but I chose the easier method
        log.info("Received GET request for all image upload logs");
        List<ImageUploadLog> logs = logService.getAllLogs();
        log.info("Returning all({}) image upload logs", logs.size());
        Collections.reverse(logs);
        return logs;
    }
}
