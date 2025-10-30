package com.selimhorri.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.domain.Order;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.exception.wrapper.OrderNotFoundException;
import com.selimhorri.app.repository.OrderRepository;
import com.selimhorri.app.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private OrderServiceImpl orderService;

	private Order order;
	private OrderDto orderDto;
	private Cart cart;
	private CartDto cartDto;

	@BeforeEach
	void setUp() {
		cart = Cart.builder()
				.cartId(1)
				.userId(1)
				.build();

		cartDto = CartDto.builder()
				.cartId(1)
				.userId(1)
				.build();

		order = Order.builder()
				.orderId(1)
				.orderDate(LocalDateTime.now())
				.orderDesc("Test Order")
				.orderFee(100.00)
				.cart(cart)
				.build();

		orderDto = OrderDto.builder()
				.orderId(1)
				.orderDate(LocalDateTime.now())
				.orderDesc("Test Order")
				.orderFee(100.00)
				.cartDto(cartDto)
				.build();
	}

	@Test
	@DisplayName("findAll - Debe retornar lista de órdenes")
	void testFindAll_Success() {
		when(orderRepository.findAll()).thenReturn(List.of(order));

		List<OrderDto> result = orderService.findAll();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Test Order", result.get(0).getOrderDesc());
		verify(orderRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("findById - Debe retornar orden por ID")
	void testFindById_Success() {
		when(orderRepository.findById(1)).thenReturn(Optional.of(order));

		OrderDto result = orderService.findById(1);

		assertNotNull(result);
		assertEquals(1, result.getOrderId());
		assertEquals("Test Order", result.getOrderDesc());
		verify(orderRepository, times(1)).findById(1);
	}

	@Test
	@DisplayName("findById - Debe lanzar excepción cuando orden no existe")
	void testFindById_NotFound() {
		when(orderRepository.findById(999)).thenReturn(Optional.empty());

		assertThrows(OrderNotFoundException.class, () -> orderService.findById(999));
		verify(orderRepository, times(1)).findById(999);
	}

	@Test
	@DisplayName("save - Debe guardar orden correctamente")
	void testSave_Success() {
		when(orderRepository.save(any(Order.class))).thenReturn(order);

		OrderDto result = orderService.save(orderDto);

		assertNotNull(result);
		assertEquals("Test Order", result.getOrderDesc());
		assertEquals(100.00, result.getOrderFee());
		verify(orderRepository, times(1)).save(any(Order.class));
	}

	@Test
	@DisplayName("deleteById - Debe eliminar orden por ID")
	void testDeleteById_Success() {
		when(orderRepository.findById(1)).thenReturn(Optional.of(order));
		doNothing().when(orderRepository).delete(any(Order.class));

		orderService.deleteById(1);

		verify(orderRepository, times(1)).findById(1);
		verify(orderRepository, times(1)).delete(any(Order.class));
	}
}
