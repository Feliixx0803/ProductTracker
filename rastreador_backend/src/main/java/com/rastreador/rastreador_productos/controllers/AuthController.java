package com.rastreador.rastreador_productos.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rastreador.rastreador_productos.dto.AuthResponseDTO;
import com.rastreador.rastreador_productos.dto.LoginDataDTO;
import com.rastreador.rastreador_productos.dto.RegisterDataDTO;
import com.rastreador.rastreador_productos.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterDataDTO request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDataDTO request){
        return ResponseEntity.ok(authService.login(request));
    }
}
