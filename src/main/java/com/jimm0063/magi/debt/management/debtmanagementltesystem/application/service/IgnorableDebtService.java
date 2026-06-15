package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.GetIgnorableDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.MarkDebtAsIgnorableUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.RemoveIgnorableDebtUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.IgnorableDebtRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.IgnorableDebt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IgnorableDebtService implements MarkDebtAsIgnorableUseCase, RemoveIgnorableDebtUseCase, GetIgnorableDebtsUseCase {
    private final IgnorableDebtRepository ignorableDebtRepository;

    public IgnorableDebtService(IgnorableDebtRepository ignorableDebtRepository) {
        this.ignorableDebtRepository = ignorableDebtRepository;
    }

    @Override
    public IgnorableDebt markAsIgnorable(String hashSum, String reason) {
        IgnorableDebt ignorableDebt = new IgnorableDebt();
        ignorableDebt.setHashSum(hashSum);
        ignorableDebt.setReason(reason);
        ignorableDebt.setActive(true);
        return ignorableDebtRepository.save(ignorableDebt);
    }

    @Override
    public void removeIgnore(String hashSum) {
        ignorableDebtRepository.deactivate(hashSum);
    }

    @Override
    public List<IgnorableDebt> getAllIgnorableDebts() {
        return ignorableDebtRepository.findAllActive();
    }
}
