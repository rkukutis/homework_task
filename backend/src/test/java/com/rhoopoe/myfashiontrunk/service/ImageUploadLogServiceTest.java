package com.rhoopoe.myfashiontrunk.service;

import com.rhoopoe.myfashiontrunk.entity.Category;
import com.rhoopoe.myfashiontrunk.entity.ImageUploadLog;
import com.rhoopoe.myfashiontrunk.enumerated.ImageItemIdentity;
import com.rhoopoe.myfashiontrunk.repository.CategoryRepository;
import com.rhoopoe.myfashiontrunk.repository.ImageUploadLogRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ImageUploadLogService.class})
@ActiveProfiles("test")
class ImageUploadLogServiceTest {
    @Autowired
    private ImageUploadLogService logService;

    @MockBean
    private ImageUploadLogRepository mockLogRepository;

    @MockBean
    private CategoryRepository mockCategoryRepository;


    @Test
    void givenInvalidArguments_thenThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> logService.createLog(null, null, null)
        );
        assertThrows(IllegalArgumentException.class,
                () -> logService.createLog("", new byte[10], ImageItemIdentity.PROHIBITED)
        );
        assertThrows(IllegalArgumentException.class,
                () -> logService.createLog("name", new byte[0], ImageItemIdentity.PROHIBITED)
        );
    }

    @Test
    @SneakyThrows
    void givenValidArguments_thenCreateLog() {
        Category fakeCategory = Category.builder().build();
        Set<Category> categorySet = Set.of(fakeCategory);
        byte[] bytes = new byte[100];
        ImageItemIdentity identity = ImageItemIdentity.UNKNOWN;
        byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
        String checksum = new BigInteger(1, hash).toString(16);
        String name = "test";

        logService.createLog(name, bytes, identity, categorySet);
        ArgumentCaptor<Set<Category>> setCaptor = ArgumentCaptor.forClass(Set.class);
        verify(mockCategoryRepository, times(1)).saveAll(setCaptor.capture());
        assertEquals(categorySet, setCaptor.getValue());
        ArgumentCaptor<ImageUploadLog> logCaptor = ArgumentCaptor.forClass(ImageUploadLog.class);
        verify(mockLogRepository, times(1)).save(logCaptor.capture());
        assertEquals(name, logCaptor.getValue().getOriginalFileName());
        assertEquals(checksum, logCaptor.getValue().getMd5Checksum());
        assertEquals(categorySet, logCaptor.getValue().getIdentifiedCategories());
        assertEquals(identity, logCaptor.getValue().getIdentifiedAs());
    }

}