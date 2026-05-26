package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.FixedExpense;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserStatusDashboard {
    private Double salary;
    private Double savings;
    private Double monthlyDebtPaymentAmount;
    private Double debtLoanAmount;
    private Double debtForLifePlanAmount;
    private Double monthlyFixedExpensesAmount;
    private Double totalMonthlyDebt;
    private Double availableIncome;
    private List<DebtAccount> userDebtAccounts;
    private List<AlmostCompletedDebtsDto> almostCompletedDebts;
    private List<FixedExpense> userFixedExpenses;
}
