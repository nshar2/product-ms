package com.roche.assessment.dao.impl;

import com.roche.assessment.dao.ProductRepository;
import com.roche.assessment.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
public class ProductRepositoryImplTest {

    private static final String TEST_SKU = "testSku";
    private static final String TEST_PRODUCT = "test product";
    private static final String TEST_PRODUCT_UPDATED = "test product - updated";
    private static Date DATE;
    private static final BigDecimal PRICE = new BigDecimal("12.45");

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() throws Exception {
        final Product product = createActiveTestProduct();
        productRepository.save(product);
    }

    @AfterEach
    public void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    public void testAddProduct() throws Exception {

        //the product has been added in the setup call

        final Product returnedProductForSku = productRepository.findBySku(TEST_SKU);

        assertEquals(TEST_PRODUCT, returnedProductForSku.getName());
        assertEquals(TEST_SKU, returnedProductForSku.getSku());
        assertEquals(DATE, returnedProductForSku.getDate());
        assertEquals(PRICE, returnedProductForSku.getPrice());
        assertEquals(false, returnedProductForSku.isDeletedFlag());
    }

    @Test
    public void testUpdateProduct() throws Exception {
        final Product returnedProductForSku = productRepository.findBySku(TEST_SKU);

        returnedProductForSku.setName(TEST_PRODUCT_UPDATED);
        productRepository.save(returnedProductForSku);

        final Product updatedProductForSku = productRepository.findBySku(TEST_SKU);

        assertEquals(TEST_PRODUCT_UPDATED, updatedProductForSku.getName());
        assertEquals(TEST_SKU, updatedProductForSku.getSku());
        assertEquals(DATE, updatedProductForSku.getDate());
        assertEquals(PRICE, updatedProductForSku.getPrice());
        assertEquals(false, updatedProductForSku.isDeletedFlag());
    }

    @Test
    public void testDeleteProduct() {
        final Product returnedProductForSku = productRepository.findBySku(TEST_SKU);
        returnedProductForSku.setDeletedFlag(true);

        productRepository.save(returnedProductForSku);

        //now this product will not be returned in active product list but will be returned in findAll
        assertEquals(1, productRepository.findAll().size());
        assertEquals(0, productRepository.findByDeletedFlagIsFalse().size());
    }

    @Test
    public void testFindAllActiveProducts() {

        //adding one more product - but an inactive one
        final Product product2 = Product.builder().sku("testSku2").name("test product2").deletedFlag(true).build();

        productRepository.save(product2);

        final List<Product> returnedProducts = productRepository.findByDeletedFlagIsFalse();

        assertEquals(1, returnedProducts.size());

        assertEquals(TEST_PRODUCT, returnedProducts.get(0).getName());
    }

    @Test
    public void testFindAllProducts() {
        //adding one more product - but an inactive one
        final Product product2 = Product.builder().sku("testSku2").name("test product2").deletedFlag(true).build();

        productRepository.save(product2);

        final List<Product> returnedProducts = productRepository.findAll();

        assertEquals(2, returnedProducts.size());
    }

    private Product createActiveTestProduct() throws Exception {
        DATE = new SimpleDateFormat("dd/MM/yyyy").parse("22/01/2020");
        return Product.builder().sku(TEST_SKU).name(TEST_PRODUCT).date(DATE).price(PRICE).build();
    }

}