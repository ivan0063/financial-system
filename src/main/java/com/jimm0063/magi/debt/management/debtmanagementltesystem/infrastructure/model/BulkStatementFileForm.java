package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/** One row of the bulk statement upload form: a file and the card it belongs to. */
@Getter
@Setter
public class BulkStatementFileForm {
    private MultipartFile file;
    private String debtAccountCode;
    private String pdfPassword;
}
