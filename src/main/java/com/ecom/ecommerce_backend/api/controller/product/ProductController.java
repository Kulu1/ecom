package com.ecom.ecommerce_backend.api.controller.product;

import com.ecom.ecommerce_backend.model.Product;
import com.ecom.ecommerce_backend.model.dao.ProductDAO;
import com.ecom.ecommerce_backend.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getProducts(){
        return productService.getProducts();
    }
}
