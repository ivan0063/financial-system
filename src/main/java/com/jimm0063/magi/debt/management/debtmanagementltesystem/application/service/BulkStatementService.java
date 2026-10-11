package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtDuplicationPreventUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.PreviewBulkStatementUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.PreviewStatementDiffUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.SyncBulkStatementUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.SyncStatementDiffUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtAccountRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.BulkItemAction;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.PdfPasswordRequiredException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementBatch;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementItem;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementUpload;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkSyncResult;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtDiff;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.StatementDiffResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SerializationUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bulk statement import: many files, each associated with one card. Extraction and diffing are
 * delegated to the single-statement v2 flow so both flows always classify debts the same way.
 */
@Service
public class BulkStatementService implements PreviewBulkStatementUseCase, SyncBulkStatementUseCase {

    private final PreviewStatementDiffUseCase previewUseCase;
    private final SyncStatementDiffUseCase syncUseCase;
    private final DebtAccountRepository debtAccountRepository;
    private final DebtRepository debtRepository;
    private final DebtDuplicationPreventUseCase hashService;

    public BulkStatementService(
            PreviewStatementDiffUseCase previewUseCase,
            SyncStatementDiffUseCase syncUseCase,
            DebtAccountRepository debtAccountRepository,
            DebtRepository debtRepository,
            DebtDuplicationPreventUseCase hashService) {
        this.previewUseCase = previewUseCase;
        this.syncUseCase = syncUseCase;
        this.debtAccountRepository = debtAccountRepository;
        this.debtRepository = debtRepository;
        this.hashService = hashService;
    }

    @Override
    public BulkStatementBatch preview(List<BulkStatementUpload> uploads, BulkItemAction defaultAction) {
        Map<String, String> fileByCard = new HashMap<>();
        List<BulkStatementItem> items = new ArrayList<>();

        for (BulkStatementUpload upload : uploads) {
            String code = upload.debtAccountCode() == null ? "" : upload.debtAccountCode().trim();
            BulkStatementItem item = new BulkStatementItem(fileName(upload.file()), code, defaultAction);
            items.add(item);

            if (code.isEmpty()) {
                item.setError("No card was associated with this file.");
                continue;
            }
            String previousFile = fileByCard.putIfAbsent(code, item.getFileName());
            if (previousFile != null) {
                resolveAccount(item);
                item.setError("This card is already associated with \"" + previousFile
                        + "\". Only one statement per card can be imported in a batch.");
                continue;
            }
            extract(item, upload.file(), upload.pdfPassword());
        }
        return new BulkStatementBatch(items);
    }

    @Override
    public void retry(BulkStatementItem item, MultipartFile file, String pdfPassword) {
        item.clearResult();
        item.setFileName(fileName(file));
        extract(item, file, pdfPassword);
    }

    @Override
    public void recompute(BulkStatementItem item) {
        if (item.getDiff() == null) return;
        StatementDiffResult diff = previewUseCase.recompute(item.getExtractedDebts(), item.getDebtAccountCode());
        item.setDiff(diff);
        fillMissingFromStatement(item);
    }

    @Override
    @Transactional
    public BulkSyncResult sync(BulkStatementBatch batch) {
        // Work on a copy: syncing mutates the debts, and the session copy must stay intact
        // if the transaction rolls back and the user retries.
        BulkStatementBatch copy = Objects.requireNonNull(SerializationUtils.clone(batch));

        List<BulkSyncResult.Entry> entries = new ArrayList<>();
        for (BulkStatementItem item : copy.items()) {
            entries.add(apply(item));
        }
        return new BulkSyncResult(entries);
    }

    // -------------------------------------------------------------------------

    private BulkSyncResult.Entry apply(BulkStatementItem item) {
        if (item.hasError()) {
            return entry(item, "—", BulkSyncResult.Outcome.FAILED, 0, 0, 0, item.getError());
        }
        if (item.getAction() == BulkItemAction.SKIP) {
            return entry(item, "—", BulkSyncResult.Outcome.SKIPPED, 0, 0, 0, "Skipped by user");
        }

        StatementDiffResult diff = item.getDiff();
        if (item.getAction() == BulkItemAction.UPDATE) {
            if (diff.totalChanges() == 0) {
                return entry(item, "UPDATE", BulkSyncResult.Outcome.NO_CHANGES, 0, 0, 0, "Already up to date");
            }
            syncUseCase.sync(diff, item.getAction().toSyncMode());
            return entry(item, "UPDATE", BulkSyncResult.Outcome.APPLIED,
                    diff.newDebts().size(), diff.updatedDebts().size(), diff.completedDebts().size(), null);
        }

        syncUseCase.sync(diff, item.getAction().toSyncMode());
        return entry(item, "SOURCE_OF_TRUTH", BulkSyncResult.Outcome.APPLIED,
                item.sourceOfTruthInsertCount(), 0, item.getExistingActiveCount(), "Rebuilt from statement");
    }

    private static BulkSyncResult.Entry entry(BulkStatementItem item, String mode, BulkSyncResult.Outcome outcome,
                                              int inserted, int updated, int deactivated, String note) {
        return new BulkSyncResult.Entry(item.getFileName(), item.getDebtAccountCode(), item.getAccountName(),
                mode, outcome, inserted, updated, deactivated, note);
    }

    private void extract(BulkStatementItem item, MultipartFile file, String pdfPassword) {
        if (resolveAccount(item).isEmpty()) {
            item.setError("Card " + item.getDebtAccountCode() + " was not found or is inactive.");
            return;
        }
        if (file == null || file.isEmpty()) {
            item.setError("The file is empty.");
            item.setRetryable(true);
            return;
        }
        try {
            StatementDiffResult diff = previewUseCase.preview(file, item.getDebtAccountCode(), blankToNull(pdfPassword));
            item.setDiff(diff);
            item.setExtractedDebts(new ArrayList<>(allExtracted(diff)));
            fillMissingFromStatement(item);
        } catch (PdfPasswordRequiredException e) {
            item.setError(e.getMessage());
            item.setNeedsPdfPassword(true);
            item.setRetryable(true);
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            if (e.getCause() != null && e.getCause().getMessage() != null) msg += " → " + e.getCause().getMessage();
            item.setError(msg);
            item.setRetryable(true);
        }
    }

    private Optional<DebtAccount> resolveAccount(BulkStatementItem item) {
        Optional<DebtAccount> account = debtAccountRepository.findDebtAccountByCodeAndActiveTrue(item.getDebtAccountCode());
        account.ifPresent(a -> {
            item.setAccountName(a.getName());
            item.setProviderName(a.getFinancialProvider() != null ? a.getFinancialProvider().getName() : null);
        });
        return account;
    }

    private void fillMissingFromStatement(BulkStatementItem item) {
        String code = item.getDebtAccountCode();
        List<Debt> existing = debtRepository.findAllDebtsByDebtAccountAndActiveTrue(code);
        existing.forEach(d -> { if (d.getHashSum() == null) d.setHashSum(hashService.getHashSum(d, code)); });

        Set<String> extractedHashes = item.getExtractedDebts().stream()
                .map(Debt::getHashSum)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        item.setExistingActiveCount(existing.size());
        item.setMissingFromStatement(new ArrayList<>(existing.stream()
                .filter(d -> !extractedHashes.contains(d.getHashSum()))
                .toList()));
    }

    private static List<Debt> allExtracted(StatementDiffResult diff) {
        return Stream.of(diff.newDebts(), diff.updatedDebts(), diff.completedDebts(),
                        diff.unchangedDebts(), diff.ignoredDebts())
                .flatMap(List::stream)
                .map(DebtDiff::getExtractedDebt)
                .toList();
    }

    private static String fileName(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) return "(unnamed file)";
        return file.getOriginalFilename();
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}
