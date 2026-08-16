package com.rastreador.rastreador_productos.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rastreador.rastreador_productos.services.ProductService;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/track")
    public void trackProduct(@RequestParam String asin) {
        String email = getAuthenticatedUserEmail();
        productService.trackProduct(asin, email);
    }

    @GetMapping("/tracked")
    public List<ProductDTO> getTrackedProducts() {
        String email = getAuthenticatedUserEmail();
        return productService.getTrackedProducts(email);
    }
    
    @DeleteMapping("/untrack")
    public void untrackProduct(@RequestParam String asin) {
        String email = getAuthenticatedUserEmail();
        productService.unTrackProduct(asin, email);
    }

    private String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
