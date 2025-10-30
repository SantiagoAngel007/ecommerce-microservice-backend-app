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

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductServiceImpl productService;

	private Product product;
	private ProductDto productDto;
	private Category category;
	private CategoryDto categoryDto;

	@BeforeEach
	void setUp() {
		category = Category.builder()
				.categoryId(1)
				.categoryTitle("Electronics")
				.imageUrl("http://image.url")
				.build();

		categoryDto = CategoryDto.builder()
				.categoryId(1)
				.categoryTitle("Electronics")
				.imageUrl("http://image.url")
				.build();

		product = Product.builder()
				.productId(1)
				.productTitle("Laptop")
				.imageUrl("http://laptop.url")
				.sku("LAPTOP-001")
				.priceUnit(999.99)
				.quantity(10)
				.category(category)
				.build();

		productDto = ProductDto.builder()
				.productId(1)
				.productTitle("Laptop")
				.imageUrl("http://laptop.url")
				.sku("LAPTOP-001")
				.priceUnit(999.99)
				.quantity(10)
				.categoryDto(categoryDto)
				.build();
	}

	@Test
	@DisplayName("findAll - Debe retornar lista de productos")
	void testFindAll_Success() {
		when(productRepository.findAll()).thenReturn(List.of(product));

		List<ProductDto> result = productService.findAll();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Laptop", result.get(0).getProductTitle());
		verify(productRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("findById - Debe retornar producto por ID")
	void testFindById_Success() {
		when(productRepository.findById(1)).thenReturn(Optional.of(product));

		ProductDto result = productService.findById(1);

		assertNotNull(result);
		assertEquals(1, result.getProductId());
		assertEquals("Laptop", result.getProductTitle());
		verify(productRepository, times(1)).findById(1);
	}

	@Test
	@DisplayName("findById - Debe lanzar excepción cuando producto no existe")
	void testFindById_NotFound() {
		when(productRepository.findById(999)).thenReturn(Optional.empty());

		assertThrows(ProductNotFoundException.class, () -> productService.findById(999));
		verify(productRepository, times(1)).findById(999);
	}

	@Test
	@DisplayName("save - Debe guardar producto correctamente")
	void testSave_Success() {
		when(productRepository.save(any(Product.class))).thenReturn(product);

		ProductDto result = productService.save(productDto);

		assertNotNull(result);
		assertEquals("Laptop", result.getProductTitle());
		assertEquals(999.99, result.getPriceUnit());
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@Test
	@DisplayName("deleteById - Debe eliminar producto por ID")
	void testDeleteById_Success() {
		when(productRepository.findById(1)).thenReturn(Optional.of(product));
		doNothing().when(productRepository).delete(any(Product.class));

		productService.deleteById(1);

		verify(productRepository, times(1)).findById(1);
		verify(productRepository, times(1)).delete(any(Product.class));
	}
}
