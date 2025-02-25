package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import mx.magi.jimm0063.financial.system.debt.application.service.DebtPaymentExcelService;
import mx.magi.jimm0063.financial.system.debt.application.service.DebtReportService;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.DebtRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class DefaultDebtReportService implements DebtReportService {
    private final DebtPaymentExcelService debtPaymentExcelService;
    private final DebtRepository debtRepository;

    public DefaultDebtReportService(DebtPaymentExcelService debtPaymentExcelService, DebtRepository debtRepository) {
        this.debtPaymentExcelService = debtPaymentExcelService;
        this.debtRepository = debtRepository;
    }

    @Override
    public byte[] generateDebtReport(String email) throws IOException {
        log.info("Generating debt report for {}", email);

        List<Debt> debts = debtRepository.findAllByDisabledFalse();
        return debtPaymentExcelService.generateExcel(debts);
    }
}
