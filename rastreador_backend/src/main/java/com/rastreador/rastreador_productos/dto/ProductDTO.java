package com.rastreador.rastreador_productos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductDTO(
    String title,
    String asin,
    String url,
    Double price,
    Double previousPrice,
    String currency,
    @JsonProperty("url_image") 
    String urlImage
) {}
