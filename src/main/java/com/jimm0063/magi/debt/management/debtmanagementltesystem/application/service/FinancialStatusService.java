package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.GetFinancialStatusUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.*;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.AlmostCompletedDebtsDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.UserStatusDashboard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.EntityNotFoundException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtSysUser;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.FixedExpense;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class FinancialStatusService implements GetFinancialStatusUseCase {
    private final UserRepository userRepository;
    private final FinancialProviderRepository financialProviderRepository;
    private final DebtAccountRepository debtAccountRepository;
    private final DebtRepository debtRepository;
    private final FixedExpenseRepository fixedExpenseRepository;

    public FinancialStatusService(UserRepository userRepository, FinancialProviderRepository financialProviderRepository,
                                  DebtAccountRepository debtAccountRepository, DebtRepository debtRepository,
                                  FixedExpenseRepository fixedExpenseRepository) {
        this.userRepository = userRepository;
        this.financialProviderRepository = financialProviderRepository;
        this.debtAccountRepository = debtAccountRepository;
        this.debtRepository = debtRepository;
        this.fixedExpenseRepository = fixedExpenseRepository;
    }

    @Override
    public UserStatusDashboard getUserStatus(String email) {
        DebtSysUser user = this.userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User " + email + " not found"));

        List<DebtAccount> userDebtAccounts = this.financialProviderRepository.findAllBySystemUser(user).stream()
                .map(debtAccountRepository::findAllByFinancialProvider)
                .flatMap(Collection::stream)
                .toList();

        List<Debt> userDebts = userDebtAccounts.stream()
                .map(DebtAccount::getCode)
                .map(debtRepository::findAllDebtsByDebtAccountAndActiveTrue)
                .flatMap(Collection::stream)
                .toList();

        //Get almost completed debts
        List<AlmostCompletedDebtsDto> almostCompletedDebts = userDebts.stream()
                .filter(debt -> (debt.getCurrentInstallment() + 1) >= debt.getMaxFinancingTerm())
                .map(debt -> {
                    AlmostCompletedDebtsDto almostCompletedDebtsDto = new AlmostCompletedDebtsDto();
                    almostCompletedDebtsDto.setCode(debt.getDebtAccount().getCode());
                    almostCompletedDebtsDto.setDescription(debt.getDescription());
                    almostCompletedDebtsDto.setMonthlyPayment(debt.getMonthlyPayment());
                    almostCompletedDebtsDto.setCurrentInstallment(debt.getCurrentInstallment());
                    almostCompletedDebtsDto.setMaxFinancingTerm(debt.getMaxFinancingTerm());

                    return almostCompletedDebtsDto;
                })
                .toList();

        //Getting Fixed Expenses
        List<FixedExpense> userFixedExpenses = fixedExpenseRepository.findAllFixedExpenseBySystemUserAndActiveTrue(user);

        // calculate amount
        Double debtMonthAmount = userDebts.stream()
                .filter(debt -> debt.getDebtType().equals(DebtTypeEnum.CARD) || debt.getDebtType().equals(DebtTypeEnum.PEOPLE))
                .mapToDouble(debt -> debt.getMonthlyPayment().doubleValue())
                .sum();

        Double debtLoanAmount = userDebts.stream()
                .filter(debt -> debt.getDebtType().equals(DebtTypeEnum.LOAN))
                .mapToDouble(debt -> debt.getMonthlyPayment().doubleValue())
                .sum();

        Double debtForLifePlanAmount = userDebts.stream()
                .filter(debt -> debt.getDebtType().equals(DebtTypeEnum.FOR_LIFE_PLAN))
                .mapToDouble(debt -> debt.getMonthlyPayment().doubleValue())
                .sum();

        Double fixedExpensesMonthAmount = userFixedExpenses.stream()
                .mapToDouble(fixedExpense -> fixedExpense.getMonthlyCost().doubleValue())
                .sum();


        Double totalMonthlyDebt = debtMonthAmount + debtLoanAmount + debtForLifePlanAmount;
        Double availableIncome = (user.getSalary() != null ? user.getSalary() : 0.0)
                - totalMonthlyDebt - fixedExpensesMonthAmount;

        UserStatusDashboard response = new UserStatusDashboard();
        response.setSalary(user.getSalary());
        response.setSavings(user.getSavings());
        response.setAlmostCompletedDebts(almostCompletedDebts);
        response.setUserFixedExpenses(userFixedExpenses);
        response.setUserDebtAccounts(userDebtAccounts);

        response.setMonthlyDebtPaymentAmount(debtMonthAmount);
        response.setDebtForLifePlanAmount(debtForLifePlanAmount);
        response.setDebtLoanAmount(debtLoanAmount);
        response.setMonthlyFixedExpensesAmount(fixedExpensesMonthAmount);
        response.setTotalMonthlyDebt(totalMonthlyDebt);
        response.setAvailableIncome(availableIncome);

        return response;
    }
}
