package com.rastreador.rastreador_productos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
    private final RestClient restClient;

    //API:
    @Value("${rapidapi.amazon-api.key}")
    private String apiKey;
    @Value("${rapidapi.amazon-api.host}")
    private String apiHost;

    public ProductServiceImpl(ObjectMapper objectMapper, ProductRepository productRepository, RestClient restClient) {
        this.objectMapper = objectMapper;
        this.productRepository = productRepository;
        this.restClient = restClient;
    }

    
    @Override
    public List<ProductDTO> searchProducts(String query) {
        try{

            JsonNode rootNode = restClient.get()
                .uri("/search?query={query}&page=1&country=ES", query)
                .header("x-rapidapi-host", apiHost.trim())
                .header("x-rapidapi-key", apiKey.trim())
                .retrieve()
                .body(JsonNode.class);
            
            List<ProductDTO> productsList = new ArrayList<>();
            
            
            JsonNode productsNode = rootNode.path("data").path("products");

            if (productsNode.isArray()) {
                for (JsonNode node : productsNode) {
                    String title = node.path("product_title").asString("");
                    String asin = node.path("asin").asString("");
                    String url = node.path("product_url").asString("");
                    Double price = parsePrice(node.path("product_price").asString(null));
                    Double previousPrice = price;
                    String currency = node.path("currency").asString("EUR");
                    String urlImage = node.path("product_photo").asString("");

                    productsList.add(new ProductDTO(title, asin, url, price, previousPrice, currency, urlImage));
                }
            }

            return productsList;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar los productos: " + e.getMessage(), e);
        }
    }

    //Parsea el precio al formato double, si no se pudo parsear lo establece como 0.0
    private Double parsePrice(String priceStr) {
    if (priceStr == null || priceStr.isBlank()) {
        return 0.0;
    }
    try {
        
        String clean = priceStr.replaceAll("[^0-9.,]", "").trim();
        
        if (clean.isEmpty()) return 0.0;

        
        if (clean.contains(".") && clean.contains(",")) {
            //Si tiene puntos y comas, indicamos que los puntos son miles y las comas decimal
            clean = clean.replace(".", "").replace(",", ".");
        } 
        //Si solo tiene coma, indicamos que la coma es el separador decimal
        else if (clean.contains(",")) {
            clean = clean.replace(",", ".");
        }

        return Double.parseDouble(clean);
    } catch (Exception e) {
        return 0.0;
    }
}

    public List<ProductDTO> getTrackedProducts() {
        List<Product> trackedProducts = productRepository.findAll();
        
        return trackedProducts.stream().map(p -> new ProductDTO(
            p.getTitle(),
            p.getAsin(),
            p.getUrlProduct(),
            p.getCurrentPrice(),
            p.getPreviousPrice(),
            p.getCurrency(),
            p.getUrlImage()
        )).toList();
    }

     //Devuelve null si no encuentra el producto   
     public ProductDTO getProductByAsin(String asin) {
        Product product = productRepository.findByAsin(asin).orElseThrow(() -> new RuntimeException("Producto no encontrado con asin: " + asin));
        return new ProductDTO(product.getTitle(), product.getAsin(), product.getUrlProduct(), product.getCurrentPrice(), product.getPreviousPrice(), product.getCurrency(), product.getUrlImage());
    }

    //Guardamos en la BBD solo los productos que queremos rastrear
    public void trackProduct(String asin) {
        //Si no esta en la base de datos, lo guardamos
        if(productRepository.findByAsin(asin) == null){
            try {
                JsonNode rootNode = restClient.get()
                .uri("/product-details?asin={asin}&country=ES", asin)
                .header("x-rapidapi-host", apiHost.trim())
                .header("x-rapidapi-key", apiKey.trim())
                .retrieve()
                .body(JsonNode.class);

                System.out.println("=== RESPUESTA DE AMAZON PARA " + asin + " ===");
            System.out.println(rootNode.toPrettyString());

                JsonNode productDataNode = rootNode.path("data");
                String title = productDataNode.path("product_title").asString("Producto sin título");
                String url = productDataNode.path("product_url").asString("");
                String urlImage = productDataNode.path("product_photo").asString("");
                Double price = parsePrice(productDataNode.path("product_price").asString(null));
                String currency = productDataNode.path("currency").asString("EUR");
            
                Product product = new Product(asin, title, url, urlImage, price, currency);
                
                productRepository.save(product);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar y rastrear el producto con ASIN " + asin + ": " + e.getMessage(), e);
            }
        }
    }
}
