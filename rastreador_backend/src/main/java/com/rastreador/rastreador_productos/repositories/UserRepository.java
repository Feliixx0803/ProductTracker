package com.rastreador.rastreador_productos.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rastreador.rastreador_productos.models.Product;
import com.rastreador.rastreador_productos.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    //Devuelve la lista de usuarios que estan siguiendo un producto determinado:
    List<User> findByProductsContaining(Product product);
}
