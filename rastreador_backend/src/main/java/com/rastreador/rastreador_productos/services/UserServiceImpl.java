package com.rastreador.rastreador_productos.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rastreador.rastreador_productos.models.Product;
import com.rastreador.rastreador_productos.models.User;
import com.rastreador.rastreador_productos.repositories.ProductRepository;
import com.rastreador.rastreador_productos.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public UserServiceImpl(UserRepository userRepo, ProductRepository productRepo){
        this.userRepository = userRepo;
        this.productRepository = productRepo;
    }

    @Transactional
    public void addProductToUser(String email, String asin){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        Product product = productRepository.findByAsin(asin).orElseThrow(() -> new RuntimeException("Producto no encontrado con asin: " + asin));

        user.addProduct(product);
        userRepository.save(user);
    }

    @Transactional
    public void removeProductFromUser(String email, String asin){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        Product product = productRepository.findByAsin(asin).orElseThrow(() -> new RuntimeException("Producto no encontrado con asin: " + asin));

        user.removeProduct(product);
        userRepository.save(user);
    }

    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}
