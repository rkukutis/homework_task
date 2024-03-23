package com.rhoopoe.myfashiontrunk.repository;

import com.rhoopoe.myfashiontrunk.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ImageRepository extends JpaRepository<ItemImage, UUID> {
}
