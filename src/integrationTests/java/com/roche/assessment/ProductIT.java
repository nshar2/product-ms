package com.roche.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roche.assessment.dao.ProductRepository;
import com.roche.assessment.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductIT {

    private static final String TEST_SKU = "testSku";
    private static final String TEST_PRODUCT = "test product";
    private static final String TEST_PRODUCT_UPDATED = "test product - updated";
    private static Date DATE;
    private static final BigDecimal PRICE = new BigDecimal("12.45");

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private ObjectMapper objectMappper;

    @Autowired
    private ProductRepository productRepository;

    private TestRestTemplate testRestTemplate;

    @Value("data/product/getAllActiveProducts.json")
    private ClassPathResource allActiveProductsJson;

    @BeforeEach
    public void setup() throws Exception {
        productRepository.deleteAll();
        productRepository.save(createActiveProduct());

        restTemplateBuilder = restTemplateBuilder.rootUri("http://localhost:" + port + "/products");
        testRestTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    @Test
    public void testCreateProduct() throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(asList(MediaType.APPLICATION_JSON));

        final Product product2 = Product.builder().sku("sku2").name("product2").date(DATE).price(PRICE).build();

        final HttpEntity<String> request = new HttpEntity<>(objectMappper.writeValueAsString(product2), headers);

        final ResponseEntity<String> response = this.testRestTemplate.postForEntity("/", request, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testUpdateProduct() throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(asList(MediaType.APPLICATION_JSON));

        //updating the product name
        final Product product2 = Product.builder().sku(TEST_SKU).name("product2").date(DATE).price(PRICE).build();

        final HttpEntity<String> request = new HttpEntity<>(objectMappper.writeValueAsString(product2), headers);

        ResponseEntity<Void> response = testRestTemplate.exchange("http://localhost:" + port + "/products/" + TEST_SKU, HttpMethod.PUT, request, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteProduct() {
        final List<Product> allActiveProductsBefore = productRepository.findByDeletedFlagIsFalse();

        assertEquals(1, allActiveProductsBefore.size());

        ResponseEntity<Void> response = testRestTemplate.exchange("http://localhost:" + port + "/products/" + TEST_SKU, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Product> allActiveProductsAfter = productRepository.findByDeletedFlagIsFalse();
        assertEquals(0, allActiveProductsAfter.size());
    }

    @Test
    public void testDeleteProduct_nonExistentProduct() {
        final List<Product> allActiveProductsBefore = productRepository.findByDeletedFlagIsFalse();

        assertEquals(1, allActiveProductsBefore.size());

        ResponseEntity<Void> response = testRestTemplate.exchange("http://localhost:" + port + "/products/unknown", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        List<Product> allActiveProductsAfter = productRepository.findByDeletedFlagIsFalse();
        assertEquals(1, allActiveProductsAfter.size());
    }

    @Test
    public void testFindAllActiveProducts() throws Exception {
        //adding one more product
        final Product product2 = Product.builder().sku("sku2").name("product2").date(DATE).price(PRICE).build();
        productRepository.save(product2);

        //adding one more product
        final Product product3 = Product.builder().sku("sku3").name("product3").date(DATE).price(PRICE).deletedFlag(true).build();
        productRepository.save(product3);

        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity("/", String.class);

        JSONAssert.assertEquals(StreamUtils.copyToString(allActiveProductsJson.getInputStream(), StandardCharsets.UTF_8), responseEntity.getBody(), true);
    }

    private Product createActiveProduct() throws Exception {
        DATE = new SimpleDateFormat("dd/MM/yyyy").parse("22/01/2020");
        return Product.builder().sku(TEST_SKU).name(TEST_PRODUCT).date(DATE).price(PRICE).build();
    }

}