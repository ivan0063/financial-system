package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.StatementDiffResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PreviewStatementDiffUseCase {
    StatementDiffResult preview(MultipartFile file, String debtAccountCode) throws IOException;
}
