package mx.magi.jimm0063.financial.system.debt.application.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ManualDebtRequest implements Serializable {
    private List<DebtModel> debts;
}
