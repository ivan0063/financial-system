package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.application.service.DebtService;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.CardDebt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.PersonLoanDebt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.CardDebtRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.DebtRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.PersonLoanDebtRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultDebtService implements DebtService {
    private final DebtRepository debtRepository;
    private final CardDebtRepository cardDebtRepository;
    private final PersonLoanDebtRepository personLoanDebtRepository;

    public DefaultDebtService(DebtRepository debtRepository,
                              CardDebtRepository cardDebtRepository,
                              PersonLoanDebtRepository personLoanDebtRepository) {
        this.debtRepository = debtRepository;
        this.cardDebtRepository = cardDebtRepository;
        this.personLoanDebtRepository = personLoanDebtRepository;
    }

    @Override
    public DebtModel deleteDebt(String debtId) {
        Debt debt = debtRepository.findById(debtId).orElseThrow(() -> new RuntimeException(String.format("Debt with id %s not found", debtId)));

        List<CardDebt> cardDebts = cardDebtRepository.findAllByDebt(debt);
        if(!cardDebts.isEmpty())
            cardDebtRepository.deleteAll(cardDebts);

        List<PersonLoanDebt> personDebts = personLoanDebtRepository.findAllByDebt(debt);
        if(!personDebts.isEmpty())
            personLoanDebtRepository.deleteAll(personDebts);

        debtRepository.delete(debt);

        return DebtModel.builder()
                .debtId(debtId)
                .name(debt.getName())
                .monthsPaid(debt.getMonthsPaid())
                .monthsFinanced(debt.getMonthsFinanced())
                .monthAmount(debt.getMonthAmount())
                .initialDebtAmount(debt.getInitialDebtAmount())
                .debtPaid(debt.getDebtPaid())
                .build();
    }
}
