package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import mx.magi.jimm0063.financial.system.debt.application.service.DebtPaymentExcelService;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class DefualtDebtPaymentExcelService implements DebtPaymentExcelService {

    private static final String[] HEADERS = {
            "Created At", "Name", "Initial Debt Amount", "Debt Paid",
            "Months Financed", "Months Paid", "Month Amount", "bank"
    };

    @Override
    public byte[] generateExcel(List<Debt> payments) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Debt Payments");
            createHeaderRow(sheet);
            fillDataRows(sheet, payments);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillDataRows(Sheet sheet, List<Debt> payments) {
        int rowNum = 1;
        for (Debt payment : payments) {
            String bankName = "";
            if(Objects.nonNull(payment.getCard()))
                bankName = payment.getCard().getCardCode();

            if(Objects.nonNull(payment.getPersonLoan()))
                bankName = payment.getPersonLoan().getLoanCode();

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(payment.getCreatedAt().toString());
            row.createCell(1).setCellValue(payment.getName());
            row.createCell(2).setCellValue(payment.getInitialDebtAmount());
            row.createCell(3).setCellValue(payment.getDebtPaid());
            row.createCell(4).setCellValue(payment.getMonthsFinanced());
            row.createCell(5).setCellValue(payment.getMonthsPaid());
            row.createCell(6).setCellValue(payment.getMonthAmount());
            row.createCell(7).setCellValue(bankName);
        }
    }
}
