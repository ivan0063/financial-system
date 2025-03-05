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
import java.util.Optional;

import static mx.magi.jimm0063.financial.system.debt.domain.enums.PdfExtractorTypes.MANUAL;

@Service
public class DefaultDebtInformationService implements DebtInformationService {
    private final CardRepository cardRepository;
    private final PersonLoanRepository personLoanRepository;
    private final FinancialStatusRepository financialStatusRepository;
    private final FixedExpenseRepository fixedExpenseRepository;
    private final DebtRepository debtRepository;

    public DefaultDebtInformationService(CardRepository cardRepository,
                                         PersonLoanRepository personLoanRepository,
                                         FinancialStatusRepository financialStatusRepository,
                                         FixedExpenseRepository fixedExpenseRepository,
                                         DebtRepository debtRepository) {
        this.cardRepository = cardRepository;
        this.personLoanRepository = personLoanRepository;
        this.financialStatusRepository = financialStatusRepository;
        this.fixedExpenseRepository = fixedExpenseRepository;
        this.debtRepository = debtRepository;
    }

    @Override
    public GlobalDebtStatus debtStatus(String email) {
        FinancialStatus userFinancialStatus = financialStatusRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("No financial status found for email: " + email));

        List<Debt> debts = debtRepository.findAllByDisabledFalse();
        List<FixedExpense> fixedExpenses = fixedExpenseRepository.findAllByFinalizedFalse();

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

        List<Debt2FinishModel> almostCompletedDebts = getAlmostCompletedDebts(debts);

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
    public CardDebtStatus debtEntityStatus(String entityId) {
        Optional<Card> cardOptional = cardRepository.findById(entityId);
        Optional<PersonLoan> personLoanOptional = personLoanRepository.findById(entityId);
        List<Debt> debts;
        CardDebtStatus cardDebtStatus = null;

        if (cardOptional.isPresent()) {
            Card card = cardRepository.findById(entityId)
                    .orElseThrow(() -> new RuntimeException("Card not found: " + entityId));

            debts = card.getCardDebts()
                    .stream()
                    .map(CardDebt::getDebt)
                    .filter(debt -> debt.getDisabled() == false)
                    .toList();

            cardDebtStatus = this.getCardDebtStatus(card, debts);
        }

        if (personLoanOptional.isPresent()) {
            PersonLoan personLoan = personLoanOptional.get();

            debts = personLoan.getPersonLoanDebts()
                    .stream()
                    .map(PersonLoanDebt::getDebt)
                    .filter(debt -> debt.getDisabled() == false)
                    .toList();

            cardDebtStatus = this.getPersonalLoanStatus(personLoan, debts);
        }

        return cardDebtStatus;
    }

    public CardDebtStatus getCardDebtStatus(Card card, List<Debt> debts) {
        List<Debt2FinishModel> almostCompletedDebts = getAlmostCompletedDebts(debts);

        double monthAmountPayemnt = debts.stream()
                .mapToDouble(Debt::getMonthAmount)
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

    public CardDebtStatus getPersonalLoanStatus(PersonLoan personLoan, List<Debt> debts) {
        List<Debt2FinishModel> almostCompletedDebts = getAlmostCompletedDebts(debts);

        double monthAmountPayment = debts.stream()
                .mapToDouble(Debt::getMonthAmount)
                .sum();

        Double totalDebtAmount = debts.stream()
                .mapToDouble(debt -> debt.getInitialDebtAmount() - debt.getDebtPaid())
                .sum();

        double availableCredit = 0.0;

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
                .accountStatementType(MANUAL)
                .almostCompletedDebts(almostCompletedDebts)
                .availableCredit(availableCredit)
                .cardName(personLoan.getLoanCode())
                .credit(0.0)
                .monthAmountPayment(monthAmountPayment)
                .totalDebtAmount(totalDebtAmount)
                .cardDebts(cardDebts)
                .build();
    }

    private List<Debt2FinishModel> getAlmostCompletedDebts(List<Debt> debts) {
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
