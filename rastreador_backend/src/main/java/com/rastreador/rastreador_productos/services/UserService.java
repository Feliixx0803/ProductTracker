package com.rastreador.rastreador_productos.services;

import java.util.Optional;

import com.rastreador.rastreador_productos.models.User;

public interface UserService {
    void addProductToUser(String email, String asin);
    void removeProductFromUser(String email, String asin);
    boolean existsByEmail(String email);
}
