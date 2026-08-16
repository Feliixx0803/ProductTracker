package com.rastreador.rastreador_productos.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.rastreador.rastreador_productos.services.ProductService;

@Component
public class TrackerScheduler {
    private final ProductService productService;
    
    public TrackerScheduler(ProductService productService) {
        this.productService = productService;
    }

    //Se ejecuta una vez al dia
    @Scheduled(fixedRate = 86400000)
    public void trackPrices() {
        System.out.println("Comprobando actualizaciones de precios en Amazon");
        productService.updateTrackedProductsPrices();
        System.out.println("Proceso de actualización finalizado.");
    }
}
