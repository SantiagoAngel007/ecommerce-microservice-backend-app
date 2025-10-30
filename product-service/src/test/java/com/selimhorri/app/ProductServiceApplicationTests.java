package com.selimhorri.app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class ProductServiceApplicationTests {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductServiceImpl productService;

	private Product product;
	private Category category;

	@BeforeEach
	void setUp() {
		category = Category.builder()
				.categoryTitle("Electronics")
				.imageUrl("http://image.url")
				.build();
		category = categoryRepository.save(category);

		product = Product.builder()
				.productTitle("Monitor")
				.imageUrl("http://monitor.url")
				.sku("MON-001")
				.priceUnit(299.99)
				.quantity(5)
				.category(category)
				.build();
	}

	@Test
	@DisplayName("findAll - Debe retornar todos los productos de base de datos")
	void testFindAll_Integration_Success() {
		Product saved = productRepository.save(product);

		List<ProductDto> result = productService.findAll();

		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.stream().anyMatch(p -> p.getProductId().equals(saved.getProductId())));
	}

	@Test
	@DisplayName("findById - Debe obtener producto guardado en base de datos")
	void testFindById_Integration_Success() {
		Product saved = productRepository.save(product);

		ProductDto result = productService.findById(saved.getProductId());

		assertNotNull(result);
		assertEquals(saved.getProductId(), result.getProductId());
		assertEquals("Monitor", result.getProductTitle());
	}

	@Test
	@DisplayName("save - Debe persistir producto en base de datos")
	void testSave_Integration_Success() {
		ProductDto dto = ProductDto.builder()
				.productTitle("Keyboard")
				.imageUrl("http://keyboard.url")
				.sku("KEY-001")
				.priceUnit(79.99)
				.quantity(20)
				.categoryDto(CategoryDto.builder()
						.categoryId(category.getCategoryId())
						.categoryTitle(category.getCategoryTitle())
						.build())
				.build();

		ProductDto result = productService.save(dto);

		assertNotNull(result.getProductId());
		assertEquals("Keyboard", result.getProductTitle());
		assertTrue(productRepository.existsById(result.getProductId()));
	}

	@Test
	@DisplayName("update - Debe actualizar producto existente")
	void testUpdate_Integration_Success() {
		Product saved = productRepository.save(product);

		ProductDto updateDto = ProductDto.builder()
				.productId(saved.getProductId())
				.productTitle("Monitor Updated")
				.imageUrl("http://monitor.url")
				.sku("MON-001")
				.priceUnit(349.99)
				.quantity(10)
				.categoryDto(CategoryDto.builder()
						.categoryId(category.getCategoryId())
						.build())
				.build();

		ProductDto result = productService.update(updateDto);

		assertEquals("Monitor Updated", result.getProductTitle());
		assertEquals(349.99, result.getPriceUnit());
	}

	@Test
	@DisplayName("deleteById - Debe eliminar producto de base de datos")
	void testDeleteById_Integration_Success() {
		Product saved = productRepository.save(product);

		productService.deleteById(saved.getProductId());

		assertFalse(productRepository.existsById(saved.getProductId()));
		assertThrows(ProductNotFoundException.class, () -> productService.findById(saved.getProductId()));
	}
}






