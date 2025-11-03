package com.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.elasticsearch.ProductDocument;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ProductSearchRepository;
import com.ecommerce.service.ProductSyncService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // ✅ Used for Home page product search
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    // ✅ Category-based paginated + filtered product listing
    @GetMapping("/{categoryId}")
    public Page<Product> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        List<Product> limitedList = productRepository.findDistinctByCategoryIdAndExactWordMatch(categoryId, search);

        int start = page * size;
        int end = Math.min(start + size, limitedList.size());

        List<Product> pageContent = (start < end) ? limitedList.subList(start, end) : new ArrayList<>();

        return new PageImpl<>(pageContent, PageRequest.of(page, size), limitedList.size());
    }

    // ✅ Product details view
    @GetMapping("/details/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ResponseEntity::ok)
      
        .orElse(ResponseEntity.notFound().build());
    }
    @Autowired
private ProductSyncService productSyncService;

@GetMapping("/sync")
public ResponseEntity<String> syncProductsToElasticsearch() {
    productSyncService.syncProductsToElasticsearch();
    return ResponseEntity.ok("Products synced to Elasticsearch successfully!");
}
@Autowired
private ProductSearchRepository productSearchRepository;

@GetMapping("/search")
public ResponseEntity<List<ProductDocument>> searchProducts(@RequestParam String keyword) {
    List<ProductDocument> results = productSearchRepository
            .findByNameContainingOrDescriptionContaining(keyword, keyword);
    return ResponseEntity.ok(results);
}


}
