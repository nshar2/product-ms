package com.roche.assessment.dao.impl;

import com.roche.assessment.dao.ProductRepository;
import com.roche.assessment.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepositoryImpl extends MongoRepository<Product, String>, ProductRepository {

    Product findBySku(String sku);

    List<Product> findByDeletedFlagIsFalse();

}