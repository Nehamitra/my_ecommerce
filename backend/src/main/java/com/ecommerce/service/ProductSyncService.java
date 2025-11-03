package com.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.elasticsearch.ProductDocument;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ProductSearchRepository;

@Service
public class ProductSyncService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    public void syncProductsToElasticsearch() {
        List<Product> products = productRepository.findAll();

        List<ProductDocument> documents = products.stream().map(product -> {
            ProductDocument doc = new ProductDocument();
            doc.setId(product.getId().toString());
            doc.setName(product.getName());
            doc.setDescription(product.getDescription());
            doc.setPrice(product.getPrice());
            doc.setCategory(product.getCategory().getName()); // just category name
            return doc;
        }).collect(Collectors.toList());

        productSearchRepository.saveAll(documents);
    }
}

