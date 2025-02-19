package mx.magi.jimm0063.financial.system.payments.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;


@Setter @Getter
public class DebtPaymentResponse implements Serializable {
    private String cardPaymentId;
    private LocalDateTime createdAt;
    private Double prevDebtPaid;
    private Integer prevMonthsPaid;
    private Double debtPaid;
    private Integer monthsPaid;
}
