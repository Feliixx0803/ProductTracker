package com.rastreador.rastreador_productos.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rastreador.rastreador_productos.dto.AuthResponseDTO;
import com.rastreador.rastreador_productos.dto.LoginDataDTO;
import com.rastreador.rastreador_productos.dto.RegisterDataDTO;
import com.rastreador.rastreador_productos.models.User;
import com.rastreador.rastreador_productos.repositories.UserRepository;

@Service
public class AuthService {
private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDTO register(RegisterDataDTO request) {
        
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("El email ya está registrado");
        }

        User user = new User(
            request.name(),
            request.email(),
            passwordEncoder.encode(request.password()) 
        );

        userRepository.save(user);

        String token = jwtService.getToken(user);
        return new AuthResponseDTO(token);
    }

    public AuthResponseDTO login(LoginDataDTO request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );

        //Solo si las credenciales son validas recuperamos el usuario de la base de datos:
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.getToken(user);
        return new AuthResponseDTO(token);
    }
}
