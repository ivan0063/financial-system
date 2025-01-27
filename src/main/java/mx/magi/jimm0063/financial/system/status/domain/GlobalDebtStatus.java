package mx.magi.jimm0063.financial.system.status.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.status.application.dto.Debt2FinishModel;
import mx.magi.jimm0063.financial.system.status.application.dto.FixedExpenseModel;

import java.io.Serializable;
import java.util.List;

@Builder
@Setter
@Getter
public class GlobalDebtStatus implements Serializable {
    private double debtMonthAmount;
    private double fixedExpensesMonthAmount;
    private double globalDebtAmount;
    private double salary;
    private double savings;
    private List<Debt2FinishModel> almostCompletedDebts;
    private List<FixedExpenseModel> fixedExpenses;
}
