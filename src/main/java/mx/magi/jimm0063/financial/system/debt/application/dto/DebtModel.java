package mx.magi.jimm0063.financial.system.debt.application.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DebtModel implements Serializable {
    private String name;
    private Double initialDebtAmount;
    private Double debtPaid;
    private Integer monthsFinanced;
    private Integer monthsPaid;
    private Double monthAmount;
}
