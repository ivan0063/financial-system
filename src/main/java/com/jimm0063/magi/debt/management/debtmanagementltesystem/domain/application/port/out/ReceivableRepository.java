package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Receivable;

import java.util.List;
import java.util.Optional;

public interface ReceivableRepository {
    List<Receivable> findAllByUser(String email);
    List<Receivable> findAllActiveByUser(String email);
    Optional<Receivable> findById(Integer id);
    Receivable save(Receivable receivable, String userEmail);
    Receivable update(Receivable receivable);
    void delete(Integer receivableId);
}
