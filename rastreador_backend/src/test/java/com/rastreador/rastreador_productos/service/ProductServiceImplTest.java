package com.rastreador.rastreador_productos.service;

import com.rastreador.rastreador_productos.dto.ProductDTO;
import com.rastreador.rastreador_productos.models.Product;
import com.rastreador.rastreador_productos.models.User;
import com.rastreador.rastreador_productos.repositories.ProductRepository;
import com.rastreador.rastreador_productos.repositories.UserRepository;
import com.rastreador.rastreador_productos.services.EmailService;
import com.rastreador.rastreador_productos.services.ProductServiceImpl;
import com.rastreador.rastreador_productos.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestClient restClient;
    @Mock
    private EmailService emailService;
    @InjectMocks
    ProductServiceImpl productService;

    private User user;
    private Product product;

    @BeforeEach
    void setup(){
        user = new User("Usuario test","test@test.com","test123");
        product = new Product(
                "B08N5WRWNW","Producto test","http://urlTest",
                "http://img.jpg",290.99, "EUR"
        );
    }

    //Debe devolver el producto mapeado a DTO cuando el ASIN existe en la base de datos
    @Test
    void shouldReturnProductDTOWhenAsinExists(){
        when(productRepository.findByAsin("B08N5WRWNW")).thenReturn(Optional.of(product));
        ProductDTO result = productService.getProductByAsin("B08N5WRWNW");

        assertNotNull(result);
        assertEquals("B08N5WRWNW", result.asin());
        assertEquals("Producto test", result.title());
        assertEquals(290.99, result.price());
    }

    //Debe lanzar RuntimeException si el producto no existe al consultar por ASIN
    @Test
    void shouldThrowExceptionWhenAsinDoesNotExist(){
        when(productRepository.findByAsin("ASIN_INEXISTENTE")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, ()-> {
            productService.getProductByAsin("ASIN_INEXISTENTE");
        });
    }

    //Debe devolver la lista de productos en seguimiento para un usuario autenticado
    @Test
    void shouldReturnTrackedProductsForGivenUser(){
        user.addProduct(product);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        List<ProductDTO> result = productService.getTrackedProducts("test@test.com");
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("B08N5WRWNW",result.get(0).asin());
    }


}
