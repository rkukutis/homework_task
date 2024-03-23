package com.rhoopoe.myfashiontrunk.repository;

import com.rhoopoe.myfashiontrunk.entity.Category;
import com.rhoopoe.myfashiontrunk.enumerated.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByType(CategoryType categoryType);
    boolean existsByNameIgnoreCase(String name);
}
