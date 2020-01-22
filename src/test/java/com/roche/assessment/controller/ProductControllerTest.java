package com.roche.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roche.assessment.model.Product;
import com.roche.assessment.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {

    private static final String TEST_SKU = "testSku";
    private static final String TEST_PRODUCT = "test product";
    private static final String TEST_PRODUCT_UPDATED = "test product - updated";
    private static Date DATE;
    private static final BigDecimal PRICE = new BigDecimal("12.45");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMappper;

    @MockBean
    private ProductService productService;

    @Test
    public void testCreateNewProduct() throws Exception {
        final Product newProduct = createActiveProduct();

        String newProductString = objectMappper.writeValueAsString(newProduct);

        mvc.perform(post("/products")
                .content(newProductString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(productService).createProduct(newProduct);
    }

    @Test
    public void testCreateNewProduct_validationFailure() throws Exception {
        final Product newProduct = Product.builder().sku(TEST_SKU).date(DATE).price(PRICE).build();

        String newProductString = objectMappper.writeValueAsString(newProduct);

        mvc.perform(post("/products")
                .content(newProductString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    public void testUpdateProduct() throws Exception {
        final Product product = createActiveProduct();

        String productString = objectMappper.writeValueAsString(product);

        mvc.perform(put("/products/{sku}", TEST_SKU)
                .content(productString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService).updateProduct(TEST_SKU, product);
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mvc.perform(delete("/products/{sku}", TEST_SKU)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService).deleteProduct(TEST_SKU);
    }

    @Test
    public void testGetAllActiveProducts() throws Exception {
        final Product product = createActiveProduct();
        final Product product2 = Product.builder().sku("TEST_SKU2").name("TEST_PRODUCT2").date(DATE).price(PRICE).build();

        when(productService.getAllActiveProducts()).thenReturn(asList(product, product2));

        mvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"sku\":\"testSku\",\"name\":\"test product\",\"price\":12.45,\"date\":\"2020-01-22T00:00:00.000+0000\"},{\"sku\":\"TEST_SKU2\",\"name\":\"TEST_PRODUCT2\",\"price\":12.45,\"date\":\"2020-01-22T00:00:00.000+0000\"}]"));

        verify(productService).getAllActiveProducts();
    }

    private Product createActiveProduct() throws Exception {
        DATE = new SimpleDateFormat("dd/MM/yyyy").parse("22/01/2020");
        return Product.builder().sku(TEST_SKU).name(TEST_PRODUCT).date(DATE).price(PRICE).build();
    }

}