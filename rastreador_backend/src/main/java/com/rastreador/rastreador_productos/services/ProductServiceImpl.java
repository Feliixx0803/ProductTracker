package com.rastreador.rastreador_productos.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.rastreador.rastreador_productos.dto.ProductDTO;
import com.rastreador.rastreador_productos.models.Product;
import com.rastreador.rastreador_productos.models.User;
import com.rastreador.rastreador_productos.repositories.ProductRepository;
import com.rastreador.rastreador_productos.repositories.UserRepository;

import jakarta.transaction.Transactional;
import tools.jackson.databind.JsonNode;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RestClient restClient;
    private final UserService userService;
    private final EmailService emailService;

    //API:
    @Value("${rapidapi.amazon-api.key}")
    private String apiKey;
    @Value("${rapidapi.amazon-api.host}")
    private String apiHost;

    public ProductServiceImpl
    (
        ProductRepository productRepository, 
        RestClient restClient, 
        UserService userService, 
        UserRepository userRepository,
        EmailService emailService
    ) {
            
        this.productRepository = productRepository;
        this.restClient = restClient;
        this.userService = userService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    
    @Override
    public List<ProductDTO> searchProducts(String query) {
        try{
            System.out.println(">>> DEBUG HOST: [" + this.apiHost + "]");
            System.out.println(">>> DEBUG KEY: [" + (this.apiKey != null ? this.apiKey.length() : "NULL") + " caracteres]");
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

    public List<ProductDTO> getTrackedProducts(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return user.getProducts().stream().map(p -> new ProductDTO(
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
    public void trackProduct(String asin, String email) {
        //Si no esta en la base de datos, lo guardamos
        if(productRepository.findByAsin(asin).isEmpty()){
            try {
                JsonNode rootNode = restClient.get()
                .uri("/product-details?asin={asin}&country=ES", asin)
                .header("x-rapidapi-host", apiHost.trim())
                .header("x-rapidapi-key", apiKey.trim())
                .retrieve()
                .body(JsonNode.class);

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
        userService.addProductToUser(email, asin);
    }

    public void unTrackProduct(String asin, String email){
        userService.removeProductFromUser(email, asin);
    }

    @Transactional
    public void updateTrackedProductsPrices(){
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            try {
                JsonNode rootNode = restClient.get()
                        .uri("/product-details?asin={asin}&country=ES", product.getAsin())
                        .header("x-rapidapi-host", apiHost.trim())
                        .header("x-rapidapi-key", apiKey.trim())
                        .retrieve()
                        .body(JsonNode.class);

                JsonNode productDataNode = rootNode.path("data");
                Double newPrice = parsePrice(productDataNode.path("product_price").asString(null));

                // Si ha cambiado el precio:
                if (newPrice > 0 && !newPrice.equals(product.getCurrentPrice())) {
                    Double oldPrice = product.getCurrentPrice();

                    product.setPreviousPrice(oldPrice);
                    product.setCurrentPrice(newPrice);
                    product.setLatestUpdate(LocalDateTime.now());
                    productRepository.save(product);

                    // Cuando hay bajada de precio notificamos al usuario:
                    if (newPrice < oldPrice) {
                        List<User> interestedUsers = userRepository.findByProductsContaining(product);
                        for (User user : interestedUsers) {
                            emailService.sendNotification(
                                    user.getEmail(),
                                    product.getTitle(),
                                    oldPrice,
                                    newPrice,
                                    product.getUrlProduct()
                            );
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error actualizando precio del producto " + product.getAsin() + ": " + e.getMessage());
            }
        }
        }



}
