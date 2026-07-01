package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.ReceivablePayment;

import java.util.List;

public interface ReceivablePaymentRepository {
    ReceivablePayment save(ReceivablePayment receivablePayment);
    List<ReceivablePayment> findAllByReceivableId(Integer receivableId);
}
