package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.ReceivableStatusDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.ReceivablePaymentReq;

public interface RecordReceivablePaymentUseCase {
    ReceivableStatusDto registerPayment(Integer receivableId, ReceivablePaymentReq receivablePaymentReq);
}
