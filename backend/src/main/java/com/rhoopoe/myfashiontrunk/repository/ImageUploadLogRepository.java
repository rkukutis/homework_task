package com.rhoopoe.myfashiontrunk.repository;

import com.rhoopoe.myfashiontrunk.entity.ImageUploadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageUploadLogRepository extends JpaRepository<ImageUploadLog, UUID> {
}
