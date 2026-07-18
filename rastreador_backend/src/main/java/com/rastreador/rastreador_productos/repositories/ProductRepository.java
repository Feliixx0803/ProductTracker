package com.rastreador.rastreador_productos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rastreador.rastreador_productos.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByAsin(String asin);
    //Product save(Product producto);
   
}
