package com.roche.assessment.service.impl;

import com.roche.assessment.dao.ProductRepository;
import com.roche.assessment.exception.NotFoundException;
import com.roche.assessment.model.Product;
import com.roche.assessment.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import({ProductServiceImpl.class})
public class ProductServiceImplTest {

    private static final String TEST_SKU = "testSku";
    private static final String TEST_PRODUCT = "test product";
    private static final String TEST_PRODUCT_UPDATED = "test product - updated";
    private static Date DATE;
    private static final BigDecimal PRICE = new BigDecimal("12.45");

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @Test
    public void testCreateProduct() throws Exception {
        final Product product = createActiveProduct();

        productService.createProduct(product);

        verify(productRepository).save(product);
    }

    @Test
    public void testUpdateProduct() throws Exception {
        final Product product = createActiveProduct();
        when(productRepository.findBySku(TEST_SKU)).thenReturn(product);

        final Product updatedProductRequested = Product.builder().sku(TEST_SKU).name(TEST_PRODUCT_UPDATED).date(DATE).price(PRICE).build();

        productService.updateProduct(TEST_SKU, updatedProductRequested);

        verify(productRepository).findBySku(TEST_SKU);

        final ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());

        final Product updatedProduct = productArgumentCaptor.getValue();

        assertEquals(TEST_SKU, updatedProduct.getSku());
        assertEquals(TEST_PRODUCT_UPDATED, updatedProduct.getName());
        assertEquals(DATE, updatedProduct.getDate());
        assertEquals(false, updatedProduct.isDeletedFlag());
        assertEquals(PRICE, updatedProduct.getPrice());
    }

    @Test
    public void testUpdateProduct_notFound() throws Exception {
        when(productRepository.findBySku(TEST_SKU)).thenReturn(null);

        final Product updatedProductRequested = Product.builder().sku(TEST_SKU).name(TEST_PRODUCT_UPDATED).date(DATE).price(PRICE).build();

        assertThrows(NotFoundException.class, () -> {
            productService.updateProduct(TEST_SKU, updatedProductRequested);
        });

        verify(productRepository).findBySku(TEST_SKU);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void testDeleteProduct() throws Exception {
        final Product product = createActiveProduct();

        when(productRepository.findBySku(TEST_SKU)).thenReturn(product);

        productService.deleteProduct(TEST_SKU);

        verify(productRepository).findBySku(TEST_SKU);

        final ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());

        final Product softDeletedProduct = productArgumentCaptor.getValue();

        assertEquals(true, softDeletedProduct.isDeletedFlag());
    }

    @Test
    public void testDeleteProduct_notFound() {
        when(productRepository.findBySku(TEST_SKU)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            productService.deleteProduct(TEST_SKU);
        });

        verify(productRepository).findBySku(TEST_SKU);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void testGetAllActiveProducts() throws Exception {
        final Product product = createActiveProduct();

        when(productRepository.findByDeletedFlagIsFalse()).thenReturn(asList(product));

        List<Product> allActiveProducts = productService.getAllActiveProducts();
        assertEquals(asList(product), allActiveProducts);
    }

    private Product createActiveProduct() throws Exception {
        DATE = new SimpleDateFormat("dd/MM/yyyy").parse("22/01/2020");
        return Product.builder().sku(TEST_SKU).name(TEST_PRODUCT).date(DATE).price(PRICE).build();
    }

}