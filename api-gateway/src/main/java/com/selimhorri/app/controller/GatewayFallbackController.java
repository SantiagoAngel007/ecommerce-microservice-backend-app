package com.selimhorri.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class GatewayFallbackController {
    @GetMapping("/product")
    public ResponseEntity<String> fallbackProduct() {
        log.error("⚠ PRODUCT-SERVICE circuito OPEN");
        return ResponseEntity.status(503).body("Product Service temporalmente no disponible");
    }
    @GetMapping("/payment")
    public ResponseEntity<String> fallbackPayment() {
        log.error("⚠ PAYMENT-SERVICE circuito OPEN");
        return ResponseEntity.status(503).body("Payment Service temporalmente no disponible");
    }
}