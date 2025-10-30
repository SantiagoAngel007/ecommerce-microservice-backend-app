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

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.exception.wrapper.PaymentNotFoundException;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.service.impl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	private Payment payment;
	private PaymentDto paymentDto;
	private OrderDto orderDto;

	@BeforeEach
	void setUp() {
		orderDto = OrderDto.builder()
				.orderId(1)
				.build();

		payment = Payment.builder()
				.paymentId(1)
				.orderId(1)
				.isPayed(true)
				.paymentStatus(PaymentStatus.COMPLETED)
				.build();

		paymentDto = PaymentDto.builder()
				.paymentId(1)
				.isPayed(true)
				.paymentStatus(PaymentStatus.COMPLETED)
				.orderDto(orderDto)
				.build();
	}

	@Test
	@DisplayName("findAll - Debe retornar lista de pagos")
	void testFindAll_Success() {
		when(paymentRepository.findAll()).thenReturn(List.of(payment));
		when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

		List<PaymentDto> result = paymentService.findAll();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.get(0).getIsPayed());
		verify(paymentRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("findById - Debe retornar pago por ID")
	void testFindById_Success() {
		when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));
		when(restTemplate.getForObject(anyString(), eq(OrderDto.class))).thenReturn(orderDto);

		PaymentDto result = paymentService.findById(1);

		assertNotNull(result);
		assertEquals(1, result.getPaymentId());
		assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
		verify(paymentRepository, times(1)).findById(1);
	}

	@Test
	@DisplayName("findById - Debe lanzar excepción cuando pago no existe")
	void testFindById_NotFound() {
		when(paymentRepository.findById(999)).thenReturn(Optional.empty());

		assertThrows(PaymentNotFoundException.class, () -> paymentService.findById(999));
		verify(paymentRepository, times(1)).findById(999);
	}

	@Test
	@DisplayName("save - Debe guardar pago correctamente")
	void testSave_Success() {
		when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

		PaymentDto result = paymentService.save(paymentDto);

		assertNotNull(result);
		assertTrue(result.getIsPayed());
		assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
		verify(paymentRepository, times(1)).save(any(Payment.class));
	}

	@Test
	@DisplayName("deleteById - Debe eliminar pago por ID")
	void testDeleteById_Success() {
		doNothing().when(paymentRepository).deleteById(1);

		paymentService.deleteById(1);

		verify(paymentRepository, times(1)).deleteById(1);
	}
}
