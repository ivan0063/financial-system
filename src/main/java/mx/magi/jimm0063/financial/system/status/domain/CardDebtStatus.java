package mx.magi.jimm0063.financial.system.status.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.status.application.dto.Debt2FinishModel;

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
    private List<Debt2FinishModel> almostCompletedDebts;
    private List<DebtModel> cardDebts;
}
