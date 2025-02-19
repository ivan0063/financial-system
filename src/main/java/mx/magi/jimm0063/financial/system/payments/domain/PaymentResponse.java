package mx.magi.jimm0063.financial.system.payments.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter @Getter
public class PaymentResponse implements Serializable {
    private String cardPaymentId;
    private LocalDateTime createdAt;
    private Double monthAmountUpdated;
    private List<DebtPaymentResponse> debtPayments;
}