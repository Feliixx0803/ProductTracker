package com.rastreador.rastreador_productos.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rastreador.rastreador_productos.services.ProductService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.rastreador.rastreador_productos.dto.ProductDTO;

@RestController
@RequestMapping("/products")
public class ProductsController {
    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/search")
    public List<ProductDTO> searchProducts(@RequestParam String query){
        return productService.searchProducts(query);
    }

}
