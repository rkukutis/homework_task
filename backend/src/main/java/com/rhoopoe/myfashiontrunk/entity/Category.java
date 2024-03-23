package com.rhoopoe.myfashiontrunk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rhoopoe.myfashiontrunk.enumerated.CategoryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.springframework.data.annotation.CreatedDate;

import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Enumerated(value = EnumType.STRING)
    private CategoryType type;

    @OneToMany(mappedBy = "category", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<CategoryAlias> aliases;

    @JsonIgnore
    @Setter
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST})
    @JoinTable(
            name = "item_image_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_image_id")
    )
    private Set<ItemImage> itemImages;

    @JsonIgnore
    @Setter
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST})
    @JoinTable(
            name = "item_image_log_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "image_upload_log_id")
    )
    private Set<ImageUploadLog> imageUploadLogs;

    public void addAlias(CategoryAlias newAlias) {
        if (aliases == null) {
            this.aliases = new HashSet<>();
            this.aliases.add(newAlias);
        } else {
            this.aliases.add(newAlias);
        }
    }
    public void addImage(ItemImage image) {
        if (itemImages == null) {
            this.itemImages = new HashSet<>();
            this.itemImages.add(image);
        } else {
            this.itemImages.add(image);
        }
    }
    public void addLog(ImageUploadLog log) {
        if (imageUploadLogs == null) {
            this.imageUploadLogs = new HashSet<>();
            this.imageUploadLogs.add(log);
        } else {
            this.imageUploadLogs.add(log);
        }
    }

    @CreatedDate
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

}
