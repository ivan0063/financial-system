package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.BulkItemAction;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementBatch;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementItem;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PreviewBulkStatementUseCase {

    /**
     * Extracts every file with the parser of the card it was associated with and diffs it
     * against the card's current debts. A file that fails is reported on its own item and
     * never aborts the rest of the batch. Nothing is persisted.
     */
    BulkStatementBatch preview(List<BulkStatementUpload> uploads, BulkItemAction defaultAction);

    /** Re-extracts a single item from a re-uploaded file (e.g. after supplying the PDF password). */
    void retry(BulkStatementItem item, MultipartFile file, String pdfPassword);

    /** Re-runs the diff for an item from its stored debts (e.g. after marking a debt as ignorable). */
    void recompute(BulkStatementItem item);
}
