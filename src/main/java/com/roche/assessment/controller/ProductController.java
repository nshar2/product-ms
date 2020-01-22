package com.roche.assessment.controller;

import com.roche.assessment.model.Product;
import com.roche.assessment.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewProduct(@RequestBody @Valid Product product) {
        productService.createProduct(product);
    }

    @RequestMapping(path = "/{sku}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProduct(@PathVariable String sku, @RequestBody @Valid Product product) {
        productService.updateProduct(sku, product);
    }

    @RequestMapping(path = "/{sku}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@PathVariable String sku) {
        productService.deleteProduct(sku);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllActiveProducts() {
        return productService.getAllActiveProducts();
    }

}