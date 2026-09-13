package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.StatementDiffResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PreviewStatementDiffUseCase {
    StatementDiffResult preview(MultipartFile file, String debtAccountCode) throws IOException;

    /** Same as {@link #preview(MultipartFile, String)} but for PDFs that need a password to open. */
    StatementDiffResult preview(MultipartFile file, String debtAccountCode, String pdfPassword) throws IOException;

    /**
     * Re-runs the diff phase only, using already-extracted debts stored in the session.
     * Called after the user marks a debt as ignorable — avoids re-uploading the file.
     */
    StatementDiffResult recompute(List<Debt> extractedDebts, String debtAccountCode);
}
