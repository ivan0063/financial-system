package mx.magi.jimm0063.financial.system.debt.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DebtModel implements Serializable {
    private String description;
    private String monthPayment;
    private String paymentMonthStatus;
}
