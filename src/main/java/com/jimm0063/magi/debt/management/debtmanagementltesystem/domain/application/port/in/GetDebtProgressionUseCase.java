package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.MonthProgressionDto;

import java.time.YearMonth;
import java.util.List;

public interface GetDebtProgressionUseCase {
    List<MonthProgressionDto> getProgression(String email, YearMonth targetMonth);
}
