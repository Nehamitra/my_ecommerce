package com.ecommerce.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    private String name;
    private String description;
    private String image;

    @Column(name = "uploaded_date")
    private LocalDate uploadedDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.uploadedDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ Getters
    public Long getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
    public LocalDate getUploadedDate() { return uploadedDate; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ✅ Setters
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setImage(String image) { this.image = image; }
    public void setUploadedDate(LocalDate uploadedDate) { this.uploadedDate = uploadedDate; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ✅ Compatibility helper (in case some service calls getId())
    public Long getId() { return categoryId; }
}
