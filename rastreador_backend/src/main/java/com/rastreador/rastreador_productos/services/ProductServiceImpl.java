package com.rastreador.rastreador_productos.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.rastreador.rastreador_productos.dto.ProductDTO;
import com.rastreador.rastreador_productos.models.Product;
import com.rastreador.rastreador_productos.repositories.ProductRepository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class ProductServiceImpl implements ProductService{

    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    
    public ProductServiceImpl(ObjectMapper objectMapper, ProductRepository productRepository) {
        this.objectMapper = objectMapper;
        this.productRepository = productRepository;
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

    /*public List<ProductDTO> getTrackedProducts() {
        return null;
    }*/

     //Devuelve null si no encuentra el producto   
     public ProductDTO getProductByAsin(String asin) {
        Product product = productRepository.findByAsin(asin);
        if(product == null){
            return null;
        }
        return new ProductDTO(product.getTitle(), product.getAsin(), product.getUrlProduct(), product.getCurrentPrice(), product.getPreviousPrice(), product.getCurrency(), product.getUrlImage());
    }

    //Guardamos en la BBD solo los productos que queremos rastrear
    public void trackProduct(String asin) {
        //Si no esta en la base de datos, lo guardamos
        if(productRepository.findByAsin(asin) == null){
            Product product = new Product(asin, "titulo","url","urlImagen",5.0, "USD");
            //TODO: cambiarlo buscando en la api
            productRepository.save(product);
        }
    }
}
