package com.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    private String name;
    private String description;
    private double price;
    private double mrp;
    private double discountedPrice;
    private int quantity;
    private String picture;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // ✅ Getters & Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    // 👇 Added this so existing code using getId() won't break
    public Long getId() {
        return productId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getMrp() { return mrp; }
    public void setMrp(double mrp) { this.mrp = mrp; }

    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
