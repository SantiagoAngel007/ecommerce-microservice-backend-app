package com.selimhorri.app;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.selimhorri.app.domain.Cart;
import com.selimhorri.app.domain.Order;
import com.selimhorri.app.dto.CartDto;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.exception.wrapper.OrderNotFoundException;
import com.selimhorri.app.repository.CartRepository;
import com.selimhorri.app.repository.OrderRepository;
import com.selimhorri.app.service.impl.OrderServiceImpl;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class OrderServiceApplicationTests {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private OrderServiceImpl orderService;

	private Order order;
	private Cart cart;

	@BeforeEach
	void setUp() {
		cart = Cart.builder()
				.userId(1)
				.build();
		cart = cartRepository.save(cart);

		order = Order.builder()
				.orderDate(LocalDateTime.now())
				.orderDesc("Integration Test Order")
				.orderFee(250.00)
				.cart(cart)
				.build();
	}

	@Test
	@DisplayName("findAll - Debe retornar todas las órdenes de base de datos")
	void testFindAll_Integration_Success() {
		Order saved = orderRepository.save(order);

		List<OrderDto> result = orderService.findAll();

		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.stream().anyMatch(o -> o.getOrderId().equals(saved.getOrderId())));
	}

	@Test
	@DisplayName("findById - Debe obtener orden guardada en base de datos")
	void testFindById_Integration_Success() {
		Order saved = orderRepository.save(order);

		OrderDto result = orderService.findById(saved.getOrderId());

		assertNotNull(result);
		assertEquals(saved.getOrderId(), result.getOrderId());
		assertEquals("Integration Test Order", result.getOrderDesc());
	}

	@Test
	@DisplayName("save - Debe persistir orden en base de datos")
	void testSave_Integration_Success() {
		OrderDto dto = OrderDto.builder()
				.orderDate(LocalDateTime.now())
				.orderDesc("New Order")
				.orderFee(150.50)
				.cartDto(CartDto.builder()
						.cartId(cart.getCartId())
						.userId(cart.getUserId())
						.build())
				.build();

		OrderDto result = orderService.save(dto);

		assertNotNull(result.getOrderId());
		assertEquals("New Order", result.getOrderDesc());
		assertTrue(orderRepository.existsById(result.getOrderId()));
	}

	@Test
	@DisplayName("update - Debe actualizar orden existente")
	void testUpdate_Integration_Success() {
		Order saved = orderRepository.save(order);

		OrderDto updateDto = OrderDto.builder()
				.orderId(saved.getOrderId())
				.orderDate(LocalDateTime.now())
				.orderDesc("Updated Order")
				.orderFee(300.00)
				.cartDto(CartDto.builder()
						.cartId(cart.getCartId())
						.build())
				.build();

		OrderDto result = orderService.update(updateDto);

		assertEquals("Updated Order", result.getOrderDesc());
		assertEquals(300.00, result.getOrderFee());
	}

	@Test
	@DisplayName("deleteById - Debe eliminar orden de base de datos")
	void testDeleteById_Integration_Success() {
		Order saved = orderRepository.save(order);

		orderService.deleteById(saved.getOrderId());

		assertFalse(orderRepository.existsById(saved.getOrderId()));
		assertThrows(OrderNotFoundException.class, () -> orderService.findById(saved.getOrderId()));
	}
}






