package com.roche.assessment.dao;

import com.roche.assessment.model.Product;

import java.util.List;

public interface ProductRepository {
    Product save(Product product);

    List<Product> findAll();

    List<Product> findByDeletedFlagIsFalse();

    Product findBySku(String sku);

    void deleteAll();

}
