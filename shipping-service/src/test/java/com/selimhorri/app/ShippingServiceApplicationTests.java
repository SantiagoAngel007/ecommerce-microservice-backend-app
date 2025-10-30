package com.selimhorri.app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.domain.OrderItem;
import com.selimhorri.app.domain.id.OrderItemId;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.OrderItemNotFoundException;
import com.selimhorri.app.repository.OrderItemRepository;
import com.selimhorri.app.service.impl.OrderItemServiceImpl;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class ShippingServiceApplicationTests {

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private OrderItemServiceImpl orderItemService;

	@MockBean
	private RestTemplate restTemplate;

	private OrderItem orderItem;
	private OrderItemId orderItemId;
	private ProductDto productDto;
	private OrderDto orderDto;

	@BeforeEach
	void setUp() {
		orderItemId = new OrderItemId(1, 1);

		productDto = ProductDto.builder()
				.productId(1)
				.productTitle("Mouse")
				.sku("MOUSE-001")
				.priceUnit(29.99)
				.quantity(50)
				.build();

		orderDto = OrderDto.builder()
				.orderId(1)
				.orderDesc("Integration Test Order")
				.orderFee(150.00)
				.build();

		orderItem = OrderItem.builder()
				.productId(1)
				.orderId(1)
				.orderedQuantity(3)
				.build();
	}

	@Test
	@DisplayName("findAll - Debe retornar todos los items de orden de base de datos")
	void testFindAll_Integration_Success() {
		OrderItem saved = orderItemRepository.save(orderItem);
		when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(productDto);
		when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

		List<OrderItemDto> result = orderItemService.findAll();

		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.stream().anyMatch(o -> o.getProductId().equals(saved.getProductId()) 
				&& o.getOrderId().equals(saved.getOrderId())));
	}

	@Test
	@DisplayName("findById - Debe obtener item de orden guardado en base de datos")
	void testFindById_Integration_Success() {
		OrderItem saved = orderItemRepository.save(orderItem);
		OrderItemId savedId = new OrderItemId(saved.getProductId(), saved.getOrderId());
		when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(productDto);
		when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

		OrderItemDto result = orderItemService.findById(savedId);

		assertNotNull(result);
		assertEquals(saved.getProductId(), result.getProductId());
		assertEquals(saved.getOrderId(), result.getOrderId());
		assertEquals(3, result.getOrderedQuantity());
	}

	@Test
	@DisplayName("save - Debe persistir item de orden en base de datos")
	void testSave_Integration_Success() {
		OrderItemDto dto = OrderItemDto.builder()
				.productId(2)
				.orderId(2)
				.orderedQuantity(5)
				.productDto(ProductDto.builder().productId(2).build())
				.orderDto(OrderDto.builder().orderId(2).build())
				.build();

		OrderItemDto result = orderItemService.save(dto);

		assertNotNull(result);
		assertEquals(2, result.getProductId());
		assertEquals(2, result.getOrderId());
		assertTrue(orderItemRepository.existsById(new OrderItemId(result.getProductId(), result.getOrderId())));
	}

	@Test
	@DisplayName("update - Debe actualizar item de orden existente")
	void testUpdate_Integration_Success() {
		OrderItem saved = orderItemRepository.save(orderItem);
		OrderItemId savedId = new OrderItemId(saved.getProductId(), saved.getOrderId());

		OrderItemDto updateDto = OrderItemDto.builder()
				.productId(saved.getProductId())
				.orderId(saved.getOrderId())
				.orderedQuantity(10)
				.productDto(ProductDto.builder().productId(saved.getProductId()).build())
				.orderDto(OrderDto.builder().orderId(saved.getOrderId()).build())
				.build();

		OrderItemDto result = orderItemService.update(updateDto);

		assertEquals(10, result.getOrderedQuantity());
		assertEquals(saved.getProductId(), result.getProductId());
	}

	@Test
	@DisplayName("deleteById - Debe eliminar item de orden de base de datos")
	void testDeleteById_Integration_Success() {
		OrderItem saved = orderItemRepository.save(orderItem);
		OrderItemId savedId = new OrderItemId(saved.getProductId(), saved.getOrderId());

		orderItemService.deleteById(savedId);

		assertFalse(orderItemRepository.existsById(savedId));
		when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(productDto);
		when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);
		assertThrows(OrderItemNotFoundException.class, () -> orderItemService.findById(savedId));
	}
}






