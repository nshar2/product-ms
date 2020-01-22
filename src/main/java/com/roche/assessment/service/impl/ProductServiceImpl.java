package com.roche.assessment.service.impl;

import com.roche.assessment.dao.ProductRepository;
import com.roche.assessment.exception.NotFoundException;
import com.roche.assessment.model.Product;
import com.roche.assessment.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void createProduct(Product product) {
        log.info("save product", product);
        productRepository.save(product);
    }

    @Override
    public void updateProduct(String sku, Product product) {
        log.info("update product", product);
        Product storedProduct = productRepository.findBySku(sku);

        if (storedProduct == null) {
            throw new NotFoundException("product by sku not found");
        }

        storedProduct.setName(product.getName());
        storedProduct.setDate(product.getDate());
        storedProduct.setPrice(product.getPrice());
        productRepository.save(storedProduct);
    }

    @Override
    public void deleteProduct(String sku) {
        log.info("delete product", sku);

        Product storedProduct = productRepository.findBySku(sku);

        if (storedProduct == null) {
            throw new NotFoundException("product by sku not found");
        }

        storedProduct.setDeletedFlag(true);
        productRepository.save(storedProduct);
    }

    @Override
    public List<Product> getAllActiveProducts() {
        log.info("getAllActiveProducts");

        return productRepository.findByDeletedFlagIsFalse();
    }

}
