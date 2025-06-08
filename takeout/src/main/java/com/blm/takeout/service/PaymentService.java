package com.blm.takeout.service;

import com.blm.takeout.dto.PaymentDTO;

public interface PaymentService {
    boolean processPayment(PaymentDTO paymentDTO);
} 