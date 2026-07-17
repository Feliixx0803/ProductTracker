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
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String asin;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String urlProducto;

    @Column(nullable = false)
    private String urlImagen;

    @Column(nullable = false)
    private Double precioActual;

    @Column(nullable = false)
    private Double precioAnterior;

    @Column(nullable = false)
    private LocalDateTime fechaUltimaActualizacion;

    public Producto(String asin, String titulo, String urlProducto, String urlImagen, Double precioActual) {
        this.asin = asin;
        this.titulo = titulo;
        this.urlProducto = urlProducto;
        this.urlImagen = urlImagen;
        this.precioActual = precioActual;
        this.precioAnterior = precioActual;
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    public Producto() {
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

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrlProducto() {
        return urlProducto;
    }

    public void setUrlProducto(String urlProducto) {
        this.urlProducto = urlProducto;
    }

    public String getUrlImagen() {
        return urlImagen;
    }
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public Double getPrecioActual() {
        return precioActual;
    }
    public void setPrecioActual(Double precioActual) {
        this.precioActual = precioActual;
    }

    public Double getPrecioAnterior() {
        return precioAnterior;
    }   
    public void setPrecioAnterior(Double precioAnterior) {
        this.precioAnterior = precioAnterior;
    }
    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }   
    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    
}
