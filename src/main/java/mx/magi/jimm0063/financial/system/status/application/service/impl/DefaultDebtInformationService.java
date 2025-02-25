package mx.magi.jimm0063.financial.system.status.application.service.impl;

import mx.magi.jimm0063.financial.system.debt.domain.dto.DebtModel;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.*;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.*;
import mx.magi.jimm0063.financial.system.status.application.dto.Debt2FinishModel;
import mx.magi.jimm0063.financial.system.status.application.dto.FixedExpenseModel;
import mx.magi.jimm0063.financial.system.status.application.service.DebtInformationService;
import mx.magi.jimm0063.financial.system.status.domain.CardDebtStatus;
import mx.magi.jimm0063.financial.system.status.domain.GlobalDebtStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultDebtInformationService implements DebtInformationService {
    private final CardRepository cardRepository;
    private final CardDebtRepository cardDebtRepository;
    private final FinancialStatusRepository financialStatusRepository;
    private final FixedExpenseRepository fixedExpenseRepository;
    private final DebtRepository debtRepository;

    public DefaultDebtInformationService(CardRepository cardRepository, FinancialStatusRepository financialStatusRepository, CardDebtRepository cardDebtRepository, FixedExpenseRepository fixedExpenseRepository, DebtRepository debtRepository) {
        this.cardRepository = cardRepository;
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

        List<FixedExpenseModel> fixedExpensesList = fixedExpenses.stream()
                .map(fixedExpense -> FixedExpenseModel.builder()
                        .costAmount(fixedExpense.getCostAmount())
                        .name(fixedExpense.getName())
                        .build())
                .toList();

        double globalDebtAmount = debts
                .stream()
                .mapToDouble(Debt::getDebtPaid)
                .sum();

        List<Debt2FinishModel> almostCompletedDebts = getAlmostComplitedDebts(debts);

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
        Card card = cardRepository.findById(cardCode)
                .orElseThrow(() -> new RuntimeException("Card not found: " + cardCode));

        List<Debt> debts = card.getCardDebts()
                .stream()
                .map(CardDebt::getDebt)
                .filter(debt -> debt.getDisabled() == false)
                .toList();
        List<Debt2FinishModel> almostCompletedDebts = getAlmostComplitedDebts(debts);

        double monthAmountPayemnt = debts.stream()
                .mapToDouble(debt -> debt.getMonthAmount())
                .sum();

        Double totalDebtAmount = debts.stream()
                .mapToDouble(debt -> debt.getInitialDebtAmount() - debt.getDebtPaid())
                .sum();

        double availableCredit = card.getCredit() - totalDebtAmount;

        List<DebtModel> cardDebts = debts.stream()
                .map(debt -> DebtModel.builder()
                            .debtId(debt.getDebtId())
                            .debtPaid(debt.getDebtPaid())
                            .initialDebtAmount(debt.getInitialDebtAmount())
                            .monthAmount(debt.getMonthAmount())
                            .monthsFinanced(debt.getMonthsFinanced())
                            .monthsPaid(debt.getMonthsPaid())
                            .name(debt.getName())
                            .build())
                .toList();

        return CardDebtStatus.builder()
                .accountStatementType(card.getFileType())
                .almostCompletedDebts(almostCompletedDebts)
                .availableCredit(availableCredit)
                .cardName(card.getCardName())
                .credit(card.getCredit())
                .monthAmountPayment(monthAmountPayemnt)
                .totalDebtAmount(totalDebtAmount)
                .cardDebts(cardDebts)
                .build();
    }

    private List<Debt2FinishModel> getAlmostComplitedDebts(List<Debt> debts) {
        return debts.stream()
                .filter(debt -> debt.getDisabled() == false)
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
    }
}
