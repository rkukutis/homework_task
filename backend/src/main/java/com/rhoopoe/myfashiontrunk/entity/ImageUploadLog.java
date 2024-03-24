package com.rhoopoe.myfashiontrunk.entity;

import com.rhoopoe.myfashiontrunk.enumerated.ImageItemIdentity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ImageUploadLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originalFileName;

    private String md5Checksum;

    @Enumerated(EnumType.STRING)
    private ImageItemIdentity identifiedAs;

    @ManyToMany(mappedBy = "imageUploadLogs")
    private Set<Category> identifiedCategories;

    @CreatedDate
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

}
