package com.rastreador.rastreador_productos.services;

import java.util.List;

import com.rastreador.rastreador_productos.dto.ProductDTO;

public interface ProductService {
    List<ProductDTO> searchProducts(String query);
    List<ProductDTO> getTrackedProducts(String email);
    ProductDTO getProductByAsin(String asin);
    void trackProduct(String asin, String email);
    void unTrackProduct(String asin, String email);
    void updateTrackedProductsPrices();
}
