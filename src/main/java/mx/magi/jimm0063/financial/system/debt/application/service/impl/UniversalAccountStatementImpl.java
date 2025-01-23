package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.application.service.AccountStatement;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("UNIVERSAL")
public class UniversalAccountStatementImpl implements AccountStatement {
    @Override
    public List<DebtModel> extractDebt(File pdfFile) {
        List<DebtModel> purchases = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // Encontrar el inicio y fin de la tabla
            String tableStart = "COMPRAS Y CARGOS DIFERIDOS A MESES SIN INTERESES";
            String tableEnd = "CARGOS,COMPRAS Y ABONOS REGULARES(NO A MESES)";
            int startIndex = text.indexOf(tableStart);
            int endIndex = text.indexOf(tableEnd);

            if (startIndex != -1 && endIndex != -1) {
                String tableText = text.substring(startIndex, endIndex);

                // Procesar cada línea de la tabla
                String[] lines = tableText.split("\n");
                for (String line : lines) {
                    // Separar por columnas: Descripción, Pago requerido, Núm. de pago
                    String[] columns = line.trim().split("\\s{2,}"); // Separador por espacios
                    if (columns.length >= 4) {
                        String description = columns[1]; // Columna de descripción
                        String monthPayment = columns[2]; // Columna de pago requerido
                        String paymentMonthStatus = columns[3]; // Columna de núm. de pago

                        // Crear y agregar un objeto PurchaseMSI
                        purchases.add(new DebtModel(description, monthPayment, paymentMonthStatus));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo PDF: " + e.getMessage());
        }

        return purchases;
    }
}