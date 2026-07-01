package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.ReceivableStatusDto;

import java.util.List;

public interface GetReceivableStatusUseCase {
    ReceivableStatusDto getStatus(Integer receivableId);
    List<ReceivableStatusDto> getAllStatusesByEmail(String email);
}
