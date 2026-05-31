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
            if (isUsableText(text)) {
                return text;
            }
        } catch (Exception ignored) {}

        return ocrFallback(pdfBytes);
    }

    /**
     * Returns true only when the extracted text contains enough real alphanumeric
     * characters to be useful. Owner-restricted PDFs return mostly U+FFFD replacement
     * characters (�), which pass a naive length check but are not parseable.
     */
    private static boolean isUsableText(String text) {
        if (text == null || text.isBlank()) return false;
        long alphanumeric = text.chars()
                .filter(c -> (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'))
                .count();
        long nonWhitespace = text.chars().filter(c -> c > ' ').count();
        // Require at least 30% of non-whitespace characters to be plain alphanumeric
        return nonWhitespace > 0 && (double) alphanumeric / nonWhitespace >= 0.30;
    }

    private String ocrFallback(byte[] pdfBytes) {
        StringBuilder sb = new StringBuilder();
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            ITesseract tess = new Tesseract();
            tess.setDatapath(resolveTessdata());
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

    /**
     * Resolves the tessdata directory. Checks TESSDATA_PREFIX env var first,
     * then falls back to the standard Debian/Ubuntu path used in the Docker image.
     */
    private static String resolveTessdata() {
        String fromEnv = System.getenv("TESSDATA_PREFIX");
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;
        return "/usr/share/tesseract-ocr/5/tessdata";
    }
}
