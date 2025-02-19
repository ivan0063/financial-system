package mx.magi.jimm0063.financial.system.payments.application;

import mx.magi.jimm0063.financial.system.payments.domain.PaymentResponse;

public interface PaymentService {
    PaymentResponse doCardPayment(String cardCode);
}
