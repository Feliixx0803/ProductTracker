package com.rastreador.rastreador_productos.services;

import java.util.List;

import com.rastreador.rastreador_productos.dto.ProductDTO;

public interface ProductService {
    List<ProductDTO> searchProducts(String query);
}
