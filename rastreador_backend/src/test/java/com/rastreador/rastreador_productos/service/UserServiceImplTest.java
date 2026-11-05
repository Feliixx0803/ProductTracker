package com.rastreador.rastreador_productos.service;

import com.rastreador.rastreador_productos.models.Product;
import com.rastreador.rastreador_productos.models.User;
import com.rastreador.rastreador_productos.repositories.ProductRepository;
import com.rastreador.rastreador_productos.repositories.UserRepository;
import com.rastreador.rastreador_productos.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User("Ana", "ana@test.com", "pass123");
        product = new Product("B091234567", "Monitor Gaming", "http://url", "http://img", 199.99, "EUR");
    }

    @Test
    //addProductToUser: Añade producto a la lista del usuario y guarda la entidad
    void shouldAddProductToUserSuccessfully() {
        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findByAsin("B091234567")).thenReturn(Optional.of(product));

        userService.addProductToUser("ana@test.com", "B091234567");

        assertTrue(user.getProducts().contains(product));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    //addProductToUser: Lanza excepción cuando el usuario no existe
    void shouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("inexistente@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.addProductToUser("inexistente@test.com", "B091234567"));
        verify(userRepository, never()).save(any());
    }

    @Test
    //removeProductFromUser: Elimina el producto del conjunto del usuario"
    void shouldRemoveProductFromUserSuccessfully() {
        user.addProduct(product);
        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(user));
        when(productRepository.findByAsin("B091234567")).thenReturn(Optional.of(product));

        userService.removeProductFromUser("ana@test.com", "B091234567");

        assertFalse(user.getProducts().contains(product));
        verify(userRepository, times(1)).save(user);
    }
}