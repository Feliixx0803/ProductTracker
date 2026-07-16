package com.rastreador.rastreador_productos.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.rastreador.rastreador_productos.dto.ProductDTO;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class ProductServiceImpl implements ProductService{

    private final ObjectMapper objectMapper;
    
    public ProductServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    
    @Override
    public List<ProductDTO> searchProducts(String query) {
        ClassPathResource resource = new ClassPathResource("mocks/amazon-search-mock.json");
        try{
            InputStream inputStream = resource.getInputStream();

            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode paidNodes = rootNode.get("results").get(0).get("content").get("results").get("paid");

            List<ProductDTO> productsList = objectMapper.convertValue(paidNodes, new TypeReference<List<ProductDTO>>(){});
            return productsList;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar los productos: " + e.getMessage(), e);
        }
    }
}
