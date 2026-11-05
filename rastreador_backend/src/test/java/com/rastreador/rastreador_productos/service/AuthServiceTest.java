package com.rastreador.rastreador_productos.service;
import com.rastreador.rastreador_productos.dto.AuthResponseDTO;
import com.rastreador.rastreador_productos.dto.LoginDataDTO;
import com.rastreador.rastreador_productos.dto.RegisterDataDTO;
import com.rastreador.rastreador_productos.models.User;
import com.rastreador.rastreador_productos.repositories.UserRepository;
import com.rastreador.rastreador_productos.services.AuthService;
import com.rastreador.rastreador_productos.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith (MockitoExtension.class)
public class AuthServiceTest{
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks 
    private AuthService authService;
    
    
    private RegisterDataDTO registerDTO;
    private LoginDataDTO loginDTO;
    private User user;

    @BeforeEach 
    void setUp() {
        registerDTO = new RegisterDataDTO("Carlos", "carlos@test.com", "password123");
        loginDTO = new LoginDataDTO("carlos@test.com", "password123");
        user = new User("Carlos", "carlos@test.com", "encodedPassword");
    }

    @Test 
    //register: Registra usuario correctamente y devuelve Token JWT
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(registerDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(registerDTO.password())).thenReturn("encodedPassword");
        when(jwtService.getToken(any(User.class))).thenReturn("jwt_token_fake");

        AuthResponseDTO response = authService.register(registerDTO);

        assertNotNull(response);
        assertEquals("jwt_token_fake", response.token());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    //register: Lanza excepcion si el email ya existe
    void shouldThrowException_WhenEmailAlreadyRegistered() {
        when(userRepository.existsByEmail(registerDTO.email())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(registerDTO));
        assertEquals("El email ya está registrado", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    //login: Autentica credenciales válidas y devuelve JWT Token
    void shouldLoginSuccessfully_WhenCredentialsAreValid() {
        when(userRepository.findByEmail(loginDTO.email())).thenReturn(Optional.of(user));
        when(jwtService.getToken(user)).thenReturn("jwt_token_fake");

        AuthResponseDTO response = authService.login(loginDTO);

        assertNotNull(response);
        assertEquals("jwt_token_fake", response.token());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    //login: Lanza excepción cuando la autenticación falla por credenciales erróneas
    void shouldThrowException_WhenAuthenticationFails() {
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginDTO));
        verify(jwtService, never()).getToken(any());
    }
}