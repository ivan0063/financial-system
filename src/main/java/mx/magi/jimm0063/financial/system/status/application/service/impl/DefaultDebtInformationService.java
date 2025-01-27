package mx.magi.jimm0063.financial.system.status.application.service.impl;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.FinancialStatus;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.FixedExpense;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.CardDebtRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.DebtRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.FinancialStatusRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.FixedExpenseRepository;
import mx.magi.jimm0063.financial.system.status.application.dto.Debt2FinishModel;
import mx.magi.jimm0063.financial.system.status.application.dto.FixedExpenseModel;
import mx.magi.jimm0063.financial.system.status.application.service.DebtInformationService;
import mx.magi.jimm0063.financial.system.status.domain.CardDebtStatus;
import mx.magi.jimm0063.financial.system.status.domain.GlobalDebtStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultDebtInformationService implements DebtInformationService {
    private final CardDebtRepository cardDebtRepository;
    private final FinancialStatusRepository financialStatusRepository;
    private final FixedExpenseRepository fixedExpenseRepository;
    private final DebtRepository debtRepository;

    public DefaultDebtInformationService(FinancialStatusRepository financialStatusRepository, CardDebtRepository cardDebtRepository, FixedExpenseRepository fixedExpenseRepository, DebtRepository debtRepository) {
        this.financialStatusRepository = financialStatusRepository;
        this.cardDebtRepository = cardDebtRepository;
        this.fixedExpenseRepository = fixedExpenseRepository;
        this.debtRepository = debtRepository;
    }

    @Override
    public GlobalDebtStatus debtStatus(String email) {
        FinancialStatus userFinancialStatus = financialStatusRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("No financial status found for email: " + email));

        List<Debt> debts = debtRepository.findAll();
        List<FixedExpense> fixedExpenses = fixedExpenseRepository.findAll();

        double fixedExpensesMonthAmount = fixedExpenses.stream()
                .mapToDouble(FixedExpense::getCostAmount)
                .sum();

        double debtMonthAmount = debts
                .stream()
                .mapToDouble(Debt::getMonthAmount)
                .sum();

        double globalDebtAmount = debts
                .stream()
                .mapToDouble(Debt::getDebtPaid)
                .sum();

        List<Debt2FinishModel> almostCompletedDebts = debts.stream()
                .filter(debt -> debt.getMonthsPaid() + 1 == debt.getMonthsFinanced())
                .map(debt -> {
                    Debt2FinishModel debt2FinishModel = Debt2FinishModel.builder()
                            .monthAmount(debt.getMonthAmount())
                            .name(debt.getName())
                            .build();
                    debt2FinishModel.creteCurrentInstallment(debt.getMonthsFinanced(), debt.getMonthsPaid());
                    return debt2FinishModel;
                })
                .toList();

        List<FixedExpenseModel> fixedExpensesList = fixedExpenses.stream()
                .map(fixedExpense -> FixedExpenseModel.builder()
                            .costAmount(fixedExpense.getCostAmount())
                            .name(fixedExpense.getName())
                            .build())
                .toList();

        return GlobalDebtStatus.builder()
                .fixedExpenses(fixedExpensesList)
                .globalDebtAmount(globalDebtAmount)
                .almostCompletedDebts(almostCompletedDebts)
                .debtMonthAmount(debtMonthAmount)
                .fixedExpensesMonthAmount(fixedExpensesMonthAmount)
                .salary(userFinancialStatus.getSalary())
                .savings(userFinancialStatus.getSavings())
                .fixedExpenses(fixedExpensesList)
                .build();
    }

    @Override
    public CardDebtStatus debtCardStatus(String cardCode) {
        return null;
    }
}
