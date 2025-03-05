package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import mx.magi.jimm0063.financial.system.debt.application.service.DebtService;
import mx.magi.jimm0063.financial.system.debt.domain.dto.DebtModel;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.DebtRepository;
import org.springframework.stereotype.Service;

@Service
public class DefaultDebtService implements DebtService {
    private final DebtRepository debtRepository;

    public DefaultDebtService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    @Override
    public DebtModel deleteDebt(String debtId) {
        Debt debt = debtRepository.findById(debtId).orElseThrow(() -> new RuntimeException(String.format("Debt with id %s not found", debtId)));

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
