package com.selimhorri.app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.dto.AddressDto;
import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.AddressRepository;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.AddressService;
import com.selimhorri.app.service.UserService;

@SpringBootTest
@ActiveProfiles("dev")
@DisplayName("User and Address Integration Tests")
class UserServiceApplicationTests {

	@Autowired
	private UserService userService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AddressRepository addressRepository;

	@BeforeEach
	void setUp() {
		addressRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("Integration - Crear usuario con múltiples direcciones")
	void testCreateUserWithMultipleAddresses() {
		// Guardar usuario
		UserDto user = UserDto.builder()
				.firstName("Santiago")
				.lastName("Angel")
				.email("santiago@example.com")
				.phone("+1234567890")
				.credentialDto(CredentialDto.builder().credentialId(1).build())
				.build();

		UserDto savedUser = userService.save(user);

		// Guardar múltiples direcciones
		AddressDto address1 = AddressDto.builder()
				.fullAddress("Calle Principal 123")
				.postalCode("28001")
				.city("Madrid")
				.userDto(UserDto.builder().userId(savedUser.getUserId()).build())
				.build();

		AddressDto address2 = AddressDto.builder()
				.fullAddress("Avenida Secundaria 456")
				.postalCode("28002")
				.city("Barcelona")
				.userDto(UserDto.builder().userId(savedUser.getUserId()).build())
				.build();

		AddressDto savedAddress1 = addressService.save(address1);
		AddressDto savedAddress2 = addressService.save(address2);

		// Verificar
		assertNotNull(savedUser.getUserId());
		assertEquals(2, addressService.findAll().size());
		assertEquals("Santiago", userService.findById(savedUser.getUserId()).getFirstName());
	}

	/* 
	@Test
	@DisplayName("Integration - Actualizar usuario y sus direcciones")
	void testUpdateUserAndItsAddresses() {
		// Crear usuario y dirección
		UserDto user = UserDto.builder()
				.firstName("Juan")
				.lastName("Perez")
				.email("juan@example.com")
				.phone("+9876543210")
				.credentialDto(CredentialDto.builder().credentialId(2).build())
				.build();

		UserDto savedUser = userService.save(user);

		AddressDto address = AddressDto.builder()
				.fullAddress("Calle Antigua 1")
				.postalCode("08001")
				.city("Valencia")
				.userDto(UserDto.builder().userId(savedUser.getUserId()).build())
				.build();

		AddressDto savedAddress = addressService.save(address);

		// Actualizar usuario
		UserDto updateUser = UserDto.builder()
				.userId(savedUser.getUserId())
				.firstName("Juan Updated")
				.lastName("Perez Updated")
				.email("juan.updated@example.com")
				.phone("+1111111111")
				.credentialDto(CredentialDto.builder().credentialId(2).build())
				.build();

		UserDto updatedUser = userService.update(savedUser.getUserId(), updateUser);

		// Actualizar dirección
		AddressDto updateAddress = AddressDto.builder()
				.addressId(savedAddress.getAddressId())
				.fullAddress("Calle Nueva 999")
				.postalCode("28099")
				.city("Madrid")
				.userDto(UserDto.builder().userId(savedUser.getUserId()).build())
				.build();

		AddressDto updatedAddress = addressService.update(savedAddress.getAddressId(), updateAddress);

		// Verificar cambios
		assertEquals("Juan Updated", updatedUser.getFirstName());
		assertEquals("Calle Nueva 999", updatedAddress.getFullAddress());
		assertEquals("Madrid", updatedAddress.getCity());
	}
	*/

	@Test
	@DisplayName("Integration - Eliminar usuario y validar direcciones")
	void testDeleteUserAndValidateAddresses() {
		// Crear usuario con 2 direcciones
		UserDto user = UserDto.builder()
				.firstName("Carlos")
				.lastName("López")
				.email("carlos@example.com")
				.phone("+5555555555")
				.credentialDto(CredentialDto.builder().credentialId(3).build())
				.build();

		UserDto savedUser = userService.save(user);

		AddressDto address1 = AddressDto.builder()
				.fullAddress("Dir 1")
				.postalCode("01001")
				.city("Bilbao")
				.userDto(UserDto.builder().userId(savedUser.getUserId()).build())
				.build();

		AddressDto address2 = AddressDto.builder()
				.fullAddress("Dir 2")
				.postalCode("41001")
				.city("Sevilla")
				.userDto(UserDto.builder().userId(savedUser.getUserId()).build())
				.build();

		addressService.save(address1);
		addressService.save(address2);

		assertEquals(2, addressService.findAll().size());

		// Eliminar usuario
		userService.deleteById(savedUser.getUserId());

		// Verificar que usuario no existe
		assertThrows(UserObjectNotFoundException.class, () -> {
			userService.findById(savedUser.getUserId());
		});
	}

	@Test
	@DisplayName("Integration - Búsqueda de usuario y obtención de direcciones asociadas")
	void testFindUserAndRetrieveAddresses() {
		// Crear usuario
		UserDto user = UserDto.builder()
				.firstName("María")
				.lastName("García")
				.email("maria@example.com")
				.phone("+3333333333")
				.credentialDto(CredentialDto.builder().credentialId(4).build())
				.build();

		UserDto savedUser = userService.save(user);

		// Guardar 3 direcciones
		for (int i = 1; i <= 3; i++) {
			AddressDto address = AddressDto.builder()
					.fullAddress("Dirección " + i)
					.postalCode("2800" + i)
					.city("Ciudad " + i)
					.userDto(UserDto.builder().userId(savedUser.getUserId()).build())
					.build();
			addressService.save(address);
		}

		// Buscar usuario
		UserDto foundUser = userService.findById(savedUser.getUserId());
		List<AddressDto> userAddresses = addressService.findAll();

		assertNotNull(foundUser);
		assertEquals("María", foundUser.getFirstName());
		assertEquals(3, userAddresses.size());
	}

	@Test
	@DisplayName("Integration - Validar persistencia y integridad referencial")
	void testDataPersistenceAndReferentialIntegrity() {
		// Crear usuario
		UserDto user = UserDto.builder()
				.firstName("Pedro")
				.lastName("Martínez")
				.email("pedro@example.com")
				.phone("+7777777777")
				.credentialDto(CredentialDto.builder().credentialId(5).build())
				.build();

		UserDto savedUser = userService.save(user);

		// Guardar dirección
		AddressDto address = AddressDto.builder()
				.fullAddress("Calle Central 100")
				.postalCode("46001")
				.city("Valencia")
				.userDto(UserDto.builder().userId(savedUser.getUserId()).build())
				.build();

		AddressDto savedAddress = addressService.save(address);

		// Obtener directamente del repositorio
		var userFromDb = userRepository.findById(savedUser.getUserId()).orElse(null);
		var addressFromDb = addressRepository.findById(savedAddress.getAddressId()).orElse(null);

		// Validar integridad
		assertNotNull(userFromDb);
		assertNotNull(addressFromDb);
		assertEquals("Pedro", userFromDb.getFirstName());
		assertEquals("Calle Central 100", addressFromDb.getFullAddress());
		assertEquals(savedUser.getUserId(), addressFromDb.getUser().getUserId());
	}

}






