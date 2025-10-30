package com.selimhorri.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.exception.wrapper.PaymentNotFoundException;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.service.impl.PaymentServiceImpl;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class PaymentServiceApplicationTests {

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentServiceImpl paymentService;

	@MockBean
	private RestTemplate restTemplate;

	private Payment payment;
	private OrderDto orderDto;

	@BeforeEach
	void setUp() {
		orderDto = OrderDto.builder()
				.orderId(1)
				.build();

		payment = Payment.builder()
				.orderId(1)
				.isPayed(false)
				.paymentStatus(PaymentStatus.IN_PROGRESS)
				.build();
	}

	@Test
	@DisplayName("findAll - Debe retornar todos los pagos de base de datos")
	void testFindAll_Integration_Success() {
		Payment saved = paymentRepository.save(payment);
		when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

		List<PaymentDto> result = paymentService.findAll();

		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.stream().anyMatch(p -> p.getPaymentId().equals(saved.getPaymentId())));
	}

	@Test
	@DisplayName("findById - Debe obtener pago guardado en base de datos")
	void testFindById_Integration_Success() {
		Payment saved = paymentRepository.save(payment);
		when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

		PaymentDto result = paymentService.findById(saved.getPaymentId());

		assertNotNull(result);
		assertEquals(saved.getPaymentId(), result.getPaymentId());
		assertEquals(PaymentStatus.IN_PROGRESS, result.getPaymentStatus());
	}

	@Test
	@DisplayName("save - Debe persistir pago en base de datos")
	void testSave_Integration_Success() {
		PaymentDto dto = PaymentDto.builder()
				.isPayed(false)
				.paymentStatus(PaymentStatus.NOT_STARTED)
				.orderDto(OrderDto.builder()
						.orderId(2)
						.build())
				.build();

		PaymentDto result = paymentService.save(dto);

		assertNotNull(result.getPaymentId());
		assertEquals(PaymentStatus.NOT_STARTED, result.getPaymentStatus());
		assertTrue(paymentRepository.existsById(result.getPaymentId()));
	}

	@Test
	@DisplayName("update - Debe actualizar pago existente")
	void testUpdate_Integration_Success() {
		Payment saved = paymentRepository.save(payment);

		PaymentDto updateDto = PaymentDto.builder()
				.paymentId(saved.getPaymentId())
				.isPayed(true)
				.paymentStatus(PaymentStatus.COMPLETED)
				.orderDto(OrderDto.builder()
						.orderId(saved.getOrderId())
						.build())
				.build();

		PaymentDto result = paymentService.update(updateDto);

		assertTrue(result.getIsPayed());
		assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
	}

	@Test
	@DisplayName("deleteById - Debe eliminar pago de base de datos")
	void testDeleteById_Integration_Success() {
		Payment saved = paymentRepository.save(payment);

		paymentService.deleteById(saved.getPaymentId());

		assertFalse(paymentRepository.existsById(saved.getPaymentId()));
		assertThrows(PaymentNotFoundException.class, () -> paymentService.findById(saved.getPaymentId()));
	}
}