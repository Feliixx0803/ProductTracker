package com.rastreador.rastreador_productos.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String asin;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String urlProduct;

    @Column(nullable = false)
    private String urlImage;

    @Column(nullable = false)
    private Double currentPrice;

    @Column(nullable = false)
    private Double previousPrice;

    @Column(nullable = false)
    private LocalDateTime latestUpdate;

    @Column(nullable = false)
    private String currency;

    public Product(String asin, String title, String urlProduct, String urlImage, Double currentPrice, String currency) {
        this.asin = asin;
        this.title = title;
        this.urlProduct = urlProduct;
        this.urlImage = urlImage;
        this.currentPrice = currentPrice;
        this.currency = currency;
        this.previousPrice = currentPrice;
        this.latestUpdate = LocalDateTime.now();
    }

    public Product() {
    }

    //Getters and setters:

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getAsin() {
        return asin;
    }
    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlProduct() {
        return urlProduct;
    }

    public void setUrlProduct(String urlProduct) {
        this.urlProduct = urlProduct;
    }

    public String getUrlImage() {
        return urlImage;
    }
    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }
    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getPreviousPrice() {
        return previousPrice;
    }   
    public void setPreviousPrice(Double previousPrice) {
        this.previousPrice = previousPrice;
    }
    public LocalDateTime getLatestUpdate() {
        return latestUpdate;
    }   
    public void setLatestUpdate(LocalDateTime latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
}
