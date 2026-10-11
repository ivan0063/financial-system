package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import org.springframework.web.multipart.MultipartFile;

/** One file of a bulk statement import, together with the card it was associated with. */
public record BulkStatementUpload(MultipartFile file, String debtAccountCode, String pdfPassword) {}
