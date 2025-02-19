package mx.magi.jimm0063.financial.system.debt.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter @Getter
public class ManualDebtRequest implements Serializable {
    private List<DebtModel> debts;
}
