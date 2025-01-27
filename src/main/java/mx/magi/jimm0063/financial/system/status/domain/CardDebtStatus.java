package mx.magi.jimm0063.financial.system.status.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;

import java.io.Serializable;
import java.util.List;

@Builder
@Setter
@Getter
public class CardDebtStatus implements Serializable {
    private double monthAmountPayment;
    private double availableCredit;
    private String cardName;
    private double credit;
    private double totalDebtAmount;
    private List<DebtModel> almostCompletedDebts;
}
