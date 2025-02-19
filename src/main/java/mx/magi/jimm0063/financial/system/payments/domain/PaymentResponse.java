package mx.magi.jimm0063.financial.system.payments.domain;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.DebtPayment;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter @Getter
public class PaymentResponse implements Serializable {
    private String cardPaymentId;
    private LocalDateTime createdAt;
    private List<DebtPaymentResponse> debtPayments;
}