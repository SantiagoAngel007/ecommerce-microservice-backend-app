package com.selimhorri.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.RoleBasedAuthority;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.CredentialRepository;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CredentialRepository credentialRepository;  // ← AGREGADO

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private Credential credential;
    private CredentialDto credentialDto;

    @BeforeEach
    void setUp() {
        // Credential sin relación bidireccional
        credential = Credential.builder()
                .credentialId(1)
                .username("testuser")
                .password("encodedPassword123")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        credentialDto = CredentialDto.builder()
                .credentialId(1)
                .username("testuser")
                .password("encodedPassword123")
                .roleBasedAuthority(RoleBasedAuthority.ROLE_USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        // User con credentialId en lugar de objeto Credential
        user = User.builder()
                .userId(1)
                .firstName("Santiago")
                .lastName("Angel")
                .email("santiago@example.com")
                .phone("+1234567890")
                .credentialId(1) 
                .build();

        userDto = UserDto.builder()
                .userId(1)
                .firstName("Santiago")
                .lastName("Angel")
                .email("santiago@example.com")
                .phone("+1234567890")
                .credentialDto(credentialDto)
                .build();
    }

    @Test
    @DisplayName("findById - Debe retornar usuario por ID")
    void testFindById_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(credentialRepository.findById(1)).thenReturn(Optional.of(credential));

        UserDto result = userService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("Santiago", result.getFirstName());
        assertEquals("santiago@example.com", result.getEmail());
        assertNotNull(result.getCredentialDto());
        assertEquals("testuser", result.getCredentialDto().getUsername());
        
        verify(userRepository, times(1)).findById(1);
        verify(credentialRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("findById - Debe lanzar excepción cuando usuario no existe")
    void testFindById_NotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(UserObjectNotFoundException.class, () -> {
            userService.findById(999);
        });
        
        verify(userRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("save - Debe guardar usuario correctamente")
    void testSave_Success() {
        when(credentialRepository.save(any(Credential.class))).thenReturn(credential);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertEquals("Santiago", result.getFirstName());
        assertEquals("santiago@example.com", result.getEmail());
        
        verify(credentialRepository, times(1)).save(any(Credential.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("update - Debe actualizar usuario por ID")
    void testUpdate_WithId_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(credentialRepository.findById(1)).thenReturn(Optional.of(credential));
        when(credentialRepository.save(any(Credential.class))).thenReturn(credential);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(1, userDto);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals("Santiago", result.getFirstName());
        
        verify(userRepository, times(1)).findById(1);
        verify(credentialRepository, times(1)).findById(1);  // ← AGREGADO (del findById interno)
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("findByUsername - Debe encontrar usuario por username")
    void testFindByUsername_Success() {
        when(userRepository.findByCredentialUsername("testuser"))
                .thenReturn(Optional.of(user));

        UserDto result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("Santiago", result.getFirstName());
        // Nota: en la nueva arquitectura, findByUsername solo retorna el user con credentialId
        // El credential completo se cargaría en findById si es necesario
        assertEquals(1, result.getCredentialDto().getCredentialId());
        
        verify(userRepository, times(1)).findByCredentialUsername("testuser");
    }

    @Test
    @DisplayName("deleteById - Debe eliminar usuario")
    void testDeleteById_Success() {
        doNothing().when(userRepository).deleteById(1);

        userService.deleteById(1);

        verify(userRepository, times(1)).deleteById(1);
    }
}



