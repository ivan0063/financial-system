package mx.magi.jimm0063.financial.system.payments.infrastructure;

import mx.magi.jimm0063.financial.system.payments.application.PaymentService;
import mx.magi.jimm0063.financial.system.payments.domain.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/do/{cardCode}")
    public ResponseEntity<PaymentResponse> doPayment(@PathVariable String cardCode) {
        return ResponseEntity.ok(this.paymentService.doCardPayment(cardCode));
    }

    @GetMapping("/latest/{cardCode}")
    public ResponseEntity<PaymentResponse> latestPayment(@PathVariable String cardCode) {
        return ResponseEntity.ok(this.paymentService.latestCardPayment(cardCode));
    }
}
