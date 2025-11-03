package com.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.elasticsearch.ProductDocument;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductSearchRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:3000")
public class SearchController {

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> globalSearch(@RequestParam String keyword, @RequestParam(required = false) String range) {
        Map<String, Object> results = new HashMap<>();
        
        // Search products using Elasticsearch or normal repository if required
        List<ProductDocument> products = productSearchRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
        results.put("products", products);
        
        // Search categories
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(keyword);
        results.put("categories", categories);

        // If a price range is provided, filter products by price
        if (range != null && !range.isEmpty()) {
            List<ProductDocument> filteredProducts = products.stream()
                .filter(product -> {
                    if (range.toLowerCase().contains("under")) {
                        // Handle "under 3000" type queries
                        String priceStr = range.replaceAll("[^0-9.]", "");
                        if (!priceStr.isEmpty()) {
                            double maxPrice = Double.parseDouble(priceStr);
                            return product.getPrice() <= maxPrice;
                        }
                    } else if (range.toLowerCase().contains("over")) {
                        // Handle "over 1000" type queries
                        String priceStr = range.replaceAll("[^0-9.]", "");
                        if (!priceStr.isEmpty()) {
                            double minPrice = Double.parseDouble(priceStr);
                            return product.getPrice() >= minPrice;
                        }
                    } else if (range.contains("-")) {
                        // Handle "1000-3000" range queries
                        String[] prices = range.split("-");
                        if (prices.length == 2) {
                            try {
                                double minPrice = Double.parseDouble(prices[0].trim());
                                double maxPrice = Double.parseDouble(prices[1].trim());
                                return product.getPrice() >= minPrice && product.getPrice() <= maxPrice;
                            } catch (NumberFormatException e) {
                                // Ignore invalid format
                            }
                        }
                    }
                    return false;
                })
                .toList();
            
            results.put("filteredProducts", filteredProducts);
        }

        return ResponseEntity.ok(results);
    }
}