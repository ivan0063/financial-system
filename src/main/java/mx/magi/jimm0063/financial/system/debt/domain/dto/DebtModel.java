package mx.magi.jimm0063.financial.system.debt.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class DebtModel implements Serializable {
    private String debtId;
    private String name;
    private Double initialDebtAmount;
    private Double debtPaid;
    private Integer monthsFinanced;
    private Integer monthsPaid;
    private Double monthAmount;
}
