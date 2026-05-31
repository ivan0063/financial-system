package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

/**
 * Extracts text from PDFs. Tries PDFBox first; falls back to Tess4j OCR
 * for owner-restricted or copy-protected PDFs that block text extraction.
 */
@Service
public class PdfTextExtractorService {

    public String extractText(byte[] pdfBytes) {
        // PDFBox handles owner-restricted PDFs with an empty user password
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(doc);
            if (text != null && text.strip().length() > 50) {
                return text;
            }
        } catch (Exception ignored) {}

        return ocrFallback(pdfBytes);
    }

    private String ocrFallback(byte[] pdfBytes) {
        StringBuilder sb = new StringBuilder();
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            ITesseract tess = new Tesseract();
            tess.setLanguage("spa");
            tess.setOcrEngineMode(1); // LSTM engine for better accuracy
            tess.setPageSegMode(6);   // Assume uniform block of text
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                BufferedImage img = renderer.renderImageWithDPI(i, 300);
                sb.append(tess.doOCR(img)).append("\n");
            }
        } catch (Exception e) {
            throw new IllegalStateException("PDF text extraction failed (PDFBox + OCR fallback)", e);
        }
        return sb.toString();
    }
}
