package com.roche.assessment.service;

import com.roche.assessment.model.Product;

import java.util.List;

public interface ProductService {
    void createProduct(Product product);

    void updateProduct(String sku, Product product);

    void deleteProduct(String sku);

    List<Product> getAllActiveProducts();

}
