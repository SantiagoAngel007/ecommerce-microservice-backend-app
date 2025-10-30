package com.selimhorri.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl - Unit Tests")
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	private User user;
	private UserDto userDto;

	@BeforeEach
	void setUp() {
		// User con credentialId desacoplado
		user = User.builder()
				.userId(1)
				.firstName("Santiago")
				.lastName("Angel")
				.email("santiago@example.com")
				.phone("+1234567890")
				.imageUrl("https://bootdey.com/img/Content/avatar/avatar7.png")
				.credentialId(1)
				.build();

		// UserDto con credentialId mapeado como CredentialDto
		CredentialDto credentialDto = CredentialDto.builder()
				.credentialId(1)
				.build();

		userDto = UserDto.builder()
				.userId(1)
				.firstName("Santiago")
				.lastName("Angel")
				.email("santiago@example.com")
				.phone("+1234567890")
				.imageUrl("https://bootdey.com/img/Content/avatar/avatar7.png")
				.credentialDto(credentialDto)
				.build();
	}

	@Test
	@DisplayName("findAll - Debe retornar lista de todos los usuarios")
	void testFindAll_Success() {
		User user2 = User.builder()
				.userId(2)
				.firstName("Juan")
				.lastName("Perez")
				.email("juan@example.com")
				.phone("+0987654321")
				.credentialId(2)
				.build();

		when(userRepository.findAll()).thenReturn(List.of(user, user2));

		List<UserDto> result = userService.findAll();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Santiago", result.get(0).getFirstName());
		assertEquals("Juan", result.get(1).getFirstName());

		verify(userRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("findAll - Debe retornar lista vacía cuando no hay usuarios")
	void testFindAll_Empty() {
		when(userRepository.findAll()).thenReturn(List.of());

		List<UserDto> result = userService.findAll();

		assertNotNull(result);
		assertEquals(0, result.size());

		verify(userRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("findById - Debe retornar usuario por ID")
	void testFindById_Success() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user));

		UserDto result = userService.findById(1);

		assertNotNull(result);
		assertEquals(1, result.getUserId());
		assertEquals("Santiago", result.getFirstName());
		assertEquals("Angel", result.getLastName());
		assertEquals("santiago@example.com", result.getEmail());
		assertEquals("+1234567890", result.getPhone());
		assertNotNull(result.getCredentialDto());
		assertEquals(1, result.getCredentialDto().getCredentialId());

		verify(userRepository, times(1)).findById(1);
	}

	@Test
	@DisplayName("findById - Debe lanzar excepción UserObjectNotFoundException cuando usuario no existe")
	void testFindById_NotFound() {
		when(userRepository.findById(999)).thenReturn(Optional.empty());

		UserObjectNotFoundException exception = assertThrows(UserObjectNotFoundException.class, () -> {
			userService.findById(999);
		});

		assertTrue(exception.getMessage().contains("User with id: 999 not found"));
		verify(userRepository, times(1)).findById(999);
	}

	@Test
	@DisplayName("save - Debe guardar nuevo usuario correctamente")
	void testSave_Success() {
		when(userRepository.save(any(User.class))).thenReturn(user);

		UserDto result = userService.save(userDto);

		assertNotNull(result);
		assertEquals(1, result.getUserId());
		assertEquals("Santiago", result.getFirstName());
		assertEquals("santiago@example.com", result.getEmail());
		assertEquals("+1234567890", result.getPhone());

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("save - Debe mapear correctamente los datos del usuario al guardar")
	void testSave_MappingCorrect() {
		UserDto newUserDto = UserDto.builder()
				.userId(3)
				.firstName("Carlos")
				.lastName("López")
				.email("carlos@example.com")
				.phone("+1111111111")
				.credentialDto(CredentialDto.builder().credentialId(3).build())
				.build();

		User savedUser = User.builder()
				.userId(3)
				.firstName("Carlos")
				.lastName("López")
				.email("carlos@example.com")
				.phone("+1111111111")
				.credentialId(3)
				.build();

		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		UserDto result = userService.save(newUserDto);

		assertEquals("Carlos", result.getFirstName());
		assertEquals("López", result.getLastName());
		assertEquals(3, result.getCredentialDto().getCredentialId());

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("update - Debe actualizar usuario sin ID")
	void testUpdate_WithoutId_Success() {
		UserDto updateDto = UserDto.builder()
				.userId(1)
				.firstName("Santiago Updated")
				.lastName("Angel Updated")
				.email("santiago.updated@example.com")
				.phone("+9999999999")
				.credentialDto(CredentialDto.builder().credentialId(1).build())
				.build();

		User updatedUser = User.builder()
				.userId(1)
				.firstName("Santiago Updated")
				.lastName("Angel Updated")
				.email("santiago.updated@example.com")
				.phone("+9999999999")
				.credentialId(1)
				.build();

		when(userRepository.save(any(User.class))).thenReturn(updatedUser);

		UserDto result = userService.update(updateDto);

		assertNotNull(result);
		assertEquals("Santiago Updated", result.getFirstName());
		assertEquals("santiago.updated@example.com", result.getEmail());

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("update - Debe actualizar usuario por ID")
	void testUpdate_WithId_Success() {
		UserDto updateDto = UserDto.builder()
				.userId(1)
				.firstName("Santiago Modified")
				.lastName("Angel Modified")
				.email("santiago.modified@example.com")
				.phone("+8888888888")
				.credentialDto(CredentialDto.builder().credentialId(1).build())
				.build();

		User updatedUser = User.builder()
				.userId(1)
				.firstName("Santiago")
				.lastName("Angel")
				.email("santiago@example.com")
				.phone("+1234567890")
				.credentialId(1)
				.build();

		when(userRepository.findById(1)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(updatedUser);

		UserDto result = userService.update(1, updateDto);

		assertNotNull(result);
		assertEquals(1, result.getUserId());

		verify(userRepository, times(1)).findById(1);
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("update - Debe lanzar excepción cuando usuario a actualizar no existe")
	void testUpdate_WithId_NotFound() {
		when(userRepository.findById(999)).thenReturn(Optional.empty());

		UserObjectNotFoundException exception = assertThrows(UserObjectNotFoundException.class, () -> {
			userService.update(999, userDto);
		});

		assertTrue(exception.getMessage().contains("User with id: 999 not found"));
		verify(userRepository, times(1)).findById(999);
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	@DisplayName("findByUsername - Debe encontrar usuario por username")
	void testFindByUsername_Success() {
		when(userRepository.findByCredentialUsername("testuser"))
				.thenReturn(Optional.of(user));

		UserDto result = userService.findByUsername("testuser");

		assertNotNull(result);
		assertEquals("Santiago", result.getFirstName());
		assertEquals("santiago@example.com", result.getEmail());
		assertEquals(1, result.getCredentialDto().getCredentialId());

		verify(userRepository, times(1)).findByCredentialUsername("testuser");
	}

	@Test
	@DisplayName("findByUsername - Debe lanzar excepción cuando username no existe")
	void testFindByUsername_NotFound() {
		when(userRepository.findByCredentialUsername("nonexistent"))
				.thenReturn(Optional.empty());

		UserObjectNotFoundException exception = assertThrows(UserObjectNotFoundException.class, () -> {
			userService.findByUsername("nonexistent");
		});

		assertTrue(exception.getMessage().contains("User with username: nonexistent not found"));
		verify(userRepository, times(1)).findByCredentialUsername("nonexistent");
	}

	@Test
	@DisplayName("deleteById - Debe eliminar usuario por ID")
	void testDeleteById_Success() {
		doNothing().when(userRepository).deleteById(1);

		userService.deleteById(1);

		verify(userRepository, times(1)).deleteById(1);
	}

	@Test
	@DisplayName("deleteById - Debe invocar deleteById del repositorio con ID correcto")
	void testDeleteById_CorrectId() {
		doNothing().when(userRepository).deleteById(5);

		userService.deleteById(5);

		verify(userRepository, times(1)).deleteById(5);
	}

	@Test
	@DisplayName("findById - Validar mapeo correcto de campos")
	void testFindById_FieldMapping() {
		User fullUser = User.builder()
				.userId(1)
				.firstName("Santiago")
				.lastName("Angel")
				.imageUrl("https://example.com/image.jpg")
				.email("santiago@example.com")
				.phone("+1234567890")
				.credentialId(1)
				.build();

		when(userRepository.findById(1)).thenReturn(Optional.of(fullUser));

		UserDto result = userService.findById(1);

		assertEquals("Santiago", result.getFirstName());
		assertEquals("Angel", result.getLastName());
		assertEquals("https://example.com/image.jpg", result.getImageUrl());
		assertEquals("santiago@example.com", result.getEmail());
		assertEquals("+1234567890", result.getPhone());
		assertEquals(1, result.getCredentialDto().getCredentialId());

		verify(userRepository, times(1)).findById(1);
	}

}