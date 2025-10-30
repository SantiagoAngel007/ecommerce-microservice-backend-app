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
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.domain.OrderItem;
import com.selimhorri.app.domain.id.OrderItemId;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.OrderItemNotFoundException;
import com.selimhorri.app.repository.OrderItemRepository;
import com.selimhorri.app.service.impl.OrderItemServiceImpl;

@ExtendWith(MockitoExtension.class)
class ShippingServiceImplTest {

	@Mock
	private OrderItemRepository orderItemRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private OrderItemServiceImpl orderItemService;

	private OrderItem orderItem;
	private OrderItemDto orderItemDto;
	private OrderItemId orderItemId;
	private ProductDto productDto;
	private OrderDto orderDto;

	@BeforeEach
	void setUp() {
		orderItemId = new OrderItemId(1, 1);

		productDto = ProductDto.builder()
				.productId(1)
				.productTitle("Laptop")
				.sku("LAPTOP-001")
				.priceUnit(999.99)
				.quantity(10)
				.build();

		orderDto = OrderDto.builder()
				.orderId(1)
				.orderDesc("Test Order")
				.orderFee(100.00)
				.build();

		orderItem = OrderItem.builder()
				.productId(1)
				.orderId(1)
				.orderedQuantity(5)
				.build();

		orderItemDto = OrderItemDto.builder()
				.productId(1)
				.orderId(1)
				.orderedQuantity(5)
				.productDto(ProductDto.builder().productId(1).build())
				.orderDto(OrderDto.builder().orderId(1).build())
				.build();
	}

	@Test
	@DisplayName("findAll - Debe retornar lista de items de orden")
	void testFindAll_Success() {
		when(orderItemRepository.findAll()).thenReturn(List.of(orderItem));
		when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(productDto);
		when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

		List<OrderItemDto> result = orderItemService.findAll();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(5, result.get(0).getOrderedQuantity());
		verify(orderItemRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("findById - Debe retornar item de orden por ID compuesto")
	void testFindById_Success() {
		when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
		when(restTemplate.getForObject(anyString(), eq(ProductDto.class))).thenReturn(productDto);
		when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

		OrderItemDto result = orderItemService.findById(orderItemId);

		assertNotNull(result);
		assertEquals(1, result.getProductId());
		assertEquals(1, result.getOrderId());
		verify(orderItemRepository, times(1)).findById(orderItemId);
	}

	@Test
	@DisplayName("findById - Debe lanzar excepción cuando item no existe")
	void testFindById_NotFound() {
		when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.empty());

		assertThrows(OrderItemNotFoundException.class, () -> orderItemService.findById(orderItemId));
		verify(orderItemRepository, times(1)).findById(orderItemId);
	}

	@Test
	@DisplayName("save - Debe guardar item de orden correctamente")
	void testSave_Success() {
		when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

		OrderItemDto result = orderItemService.save(orderItemDto);

		assertNotNull(result);
		assertEquals(5, result.getOrderedQuantity());
		assertEquals(1, result.getProductId());
		assertEquals(1, result.getOrderId());
		verify(orderItemRepository, times(1)).save(any(OrderItem.class));
	}

	@Test
	@DisplayName("deleteById - Debe eliminar item de orden por ID compuesto")
	void testDeleteById_Success() {
		doNothing().when(orderItemRepository).deleteById(orderItemId);

		orderItemService.deleteById(orderItemId);

		verify(orderItemRepository, times(1)).deleteById(orderItemId);
	}
}