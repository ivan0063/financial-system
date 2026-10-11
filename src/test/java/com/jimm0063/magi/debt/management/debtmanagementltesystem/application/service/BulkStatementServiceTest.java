package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtDuplicationPreventUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.PreviewStatementDiffUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.SyncStatementDiffUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtAccountRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.AccountStatementType;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.BulkItemAction;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtDiffStatus;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.SyncMode;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.PdfPasswordRequiredException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementBatch;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementItem;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementUpload;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkSyncResult;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtDiff;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.FinancialProvider;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.StatementDiffResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkStatementServiceTest {

    @Mock PreviewStatementDiffUseCase previewUseCase;
    @Mock SyncStatementDiffUseCase syncUseCase;
    @Mock DebtAccountRepository debtAccountRepository;
    @Mock DebtRepository debtRepository;
    @Mock DebtDuplicationPreventUseCase hashService;

    BulkStatementService service;

    private static final String VISA = "VISA";
    private static final String AMEX = "AMEX";

    @BeforeEach
    void setUp() {
        service = new BulkStatementService(previewUseCase, syncUseCase, debtAccountRepository, debtRepository, hashService);
        lenient().when(debtAccountRepository.findDebtAccountByCodeAndActiveTrue(VISA)).thenReturn(Optional.of(account(VISA, "Visa Oro")));
        lenient().when(debtAccountRepository.findDebtAccountByCodeAndActiveTrue(AMEX)).thenReturn(Optional.of(account(AMEX, "Amex Gold")));
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private static DebtAccount account(String code, String name) {
        FinancialProvider provider = new FinancialProvider();
        provider.setName("Bank");
        DebtAccount a = new DebtAccount();
        a.setCode(code);
        a.setName(name);
        a.setFinancialProvider(provider);
        return a;
    }

    private static Debt debt(String desc, String hash, int current, int max) {
        Debt d = new Debt();
        d.setDescription(desc);
        d.setHashSum(hash);
        d.setMonthlyPayment(BigDecimal.TEN);
        d.setCurrentInstallment(current);
        d.setMaxFinancingTerm(max);
        d.setActive(true);
        return d;
    }

    private static MockMultipartFile file(String name) {
        return new MockMultipartFile("file", name, "application/pdf", new byte[]{1, 2, 3});
    }

    private static StatementDiffResult diff(String code, List<DebtDiff> news, List<DebtDiff> updated,
                                            List<DebtDiff> completed, List<DebtDiff> unchanged) {
        return new StatementDiffResult(code, AccountStatementType.UNIVERSAL,
                news, updated, completed, unchanged, List.of());
    }

    // ── preview ───────────────────────────────────────────────────────────────

    @Test
    void preview_extracts_each_file_with_its_card_and_reports_debts_missing_from_statement() throws Exception {
        Debt fresh = debt("New TV", "h-new", 1, 12);
        Debt stored = debt("Old laptop", "h-old", 3, 12);
        Debt gone = debt("Phone", "h-gone", 5, 6);
        Debt advanced = debt("Old laptop", "h-old", 4, 12);
        StatementDiffResult visaDiff = diff(VISA,
                List.of(new DebtDiff(fresh, null, DebtDiffStatus.NEW)),
                List.of(new DebtDiff(advanced, stored, DebtDiffStatus.UPDATED)),
                List.of(), List.of());
        when(previewUseCase.preview(any(), eq(VISA), isNull())).thenReturn(visaDiff);
        when(debtRepository.findAllDebtsByDebtAccountAndActiveTrue(VISA)).thenReturn(List.of(stored, gone));

        BulkStatementBatch batch = service.preview(
                List.of(new BulkStatementUpload(file("visa.pdf"), VISA, "")), BulkItemAction.UPDATE);

        BulkStatementItem item = batch.items().get(0);
        assertThat(item.hasError()).isFalse();
        assertThat(item.getFileName()).isEqualTo("visa.pdf");
        assertThat(item.getAccountName()).isEqualTo("Visa Oro");
        assertThat(item.getProviderName()).isEqualTo("Bank");
        assertThat(item.getAction()).isEqualTo(BulkItemAction.UPDATE);
        assertThat(item.getExtractedDebts()).containsExactlyInAnyOrder(fresh, advanced);
        assertThat(item.getExistingActiveCount()).isEqualTo(2);
        assertThat(item.getMissingFromStatement()).containsExactly(gone);
        assertThat(batch.totalNew()).isEqualTo(1);
        assertThat(batch.totalUpdated()).isEqualTo(1);
    }

    @Test
    void preview_failure_in_one_file_does_not_abort_the_others() throws Exception {
        when(previewUseCase.preview(any(), eq(VISA), isNull()))
                .thenThrow(new PdfPasswordRequiredException("PDF is password protected", false));
        when(previewUseCase.preview(any(), eq(AMEX), isNull()))
                .thenReturn(diff(AMEX, List.of(), List.of(), List.of(), List.of()));

        BulkStatementBatch batch = service.preview(List.of(
                new BulkStatementUpload(file("visa.pdf"), VISA, null),
                new BulkStatementUpload(file("amex.pdf"), AMEX, null)), BulkItemAction.SOURCE_OF_TRUTH);

        BulkStatementItem visa = batch.items().get(0);
        assertThat(visa.hasError()).isTrue();
        assertThat(visa.isNeedsPdfPassword()).isTrue();
        assertThat(visa.isRetryable()).isTrue();
        assertThat(visa.isActionable()).isFalse();

        BulkStatementItem amex = batch.items().get(1);
        assertThat(amex.hasError()).isFalse();
        assertThat(amex.getAction()).isEqualTo(BulkItemAction.SOURCE_OF_TRUTH);
        assertThat(batch.failedCount()).isEqualTo(1);
        assertThat(batch.actionableCount()).isEqualTo(1);
    }

    @Test
    void preview_rejects_a_second_file_for_the_same_card_and_files_without_card() throws Exception {
        when(previewUseCase.preview(any(), eq(VISA), isNull()))
                .thenReturn(diff(VISA, List.of(), List.of(), List.of(), List.of()));

        BulkStatementBatch batch = service.preview(List.of(
                new BulkStatementUpload(file("visa-may.pdf"), VISA, null),
                new BulkStatementUpload(file("visa-june.pdf"), VISA, null),
                new BulkStatementUpload(file("unknown.pdf"), " ", null)), BulkItemAction.UPDATE);

        assertThat(batch.items().get(0).hasError()).isFalse();
        assertThat(batch.items().get(1).getError()).contains("visa-may.pdf");
        assertThat(batch.items().get(1).isRetryable()).isFalse();
        assertThat(batch.items().get(2).getError()).contains("No card");
        verify(previewUseCase, times(1)).preview(any(), eq(VISA), isNull());
    }

    @Test
    void retry_clears_the_error_and_reextracts_with_the_password() throws Exception {
        BulkStatementItem item = new BulkStatementItem("visa.pdf", VISA, BulkItemAction.UPDATE);
        item.setError("PDF is password protected");
        item.setNeedsPdfPassword(true);
        item.setRetryable(true);
        when(previewUseCase.preview(any(), eq(VISA), eq("secret")))
                .thenReturn(diff(VISA, List.of(), List.of(), List.of(), List.of()));

        service.retry(item, file("visa-unlocked.pdf"), "secret");

        assertThat(item.hasError()).isFalse();
        assertThat(item.isNeedsPdfPassword()).isFalse();
        assertThat(item.getFileName()).isEqualTo("visa-unlocked.pdf");
        assertThat(item.getDiff()).isNotNull();
    }

    // ── sync ──────────────────────────────────────────────────────────────────

    @Test
    void sync_applies_each_card_with_its_own_mode_and_skips_failed_skipped_and_unchanged_cards() {
        Debt fresh = debt("New TV", "h-new", 1, 12);
        BulkStatementItem update = new BulkStatementItem("visa.pdf", VISA, BulkItemAction.UPDATE);
        update.setDiff(diff(VISA, List.of(new DebtDiff(fresh, null, DebtDiffStatus.NEW)), List.of(), List.of(), List.of()));

        BulkStatementItem rebuild = new BulkStatementItem("amex.pdf", AMEX, BulkItemAction.SOURCE_OF_TRUTH);
        rebuild.setDiff(diff(AMEX, List.of(), List.of(), List.of(),
                List.of(new DebtDiff(debt("Gym", "h-gym", 2, 3), debt("Gym", "h-gym", 2, 3), DebtDiffStatus.UNCHANGED))));
        rebuild.setExistingActiveCount(4);

        BulkStatementItem noChanges = new BulkStatementItem("mc.pdf", "MC", BulkItemAction.UPDATE);
        noChanges.setDiff(diff("MC", List.of(), List.of(), List.of(), List.of()));

        BulkStatementItem skipped = new BulkStatementItem("other.pdf", "OTHER", BulkItemAction.SKIP);
        skipped.setDiff(diff("OTHER", List.of(new DebtDiff(fresh, null, DebtDiffStatus.NEW)), List.of(), List.of(), List.of()));

        BulkStatementItem failed = new BulkStatementItem("broken.pdf", "BROKEN", BulkItemAction.UPDATE);
        failed.setError("boom");

        BulkStatementBatch batch = new BulkStatementBatch(new ArrayList<>(List.of(update, rebuild, noChanges, skipped, failed)));

        BulkSyncResult result = service.sync(batch);

        ArgumentCaptor<StatementDiffResult> diffs = ArgumentCaptor.forClass(StatementDiffResult.class);
        ArgumentCaptor<SyncMode> modes = ArgumentCaptor.forClass(SyncMode.class);
        verify(syncUseCase, times(2)).sync(diffs.capture(), modes.capture());
        assertThat(diffs.getAllValues()).extracting(StatementDiffResult::debtAccountCode).containsExactly(VISA, AMEX);
        assertThat(modes.getAllValues()).containsExactly(SyncMode.UPDATE, SyncMode.SOURCE_OF_TRUTH);

        assertThat(result.entries()).extracting(BulkSyncResult.Entry::outcome).containsExactly(
                BulkSyncResult.Outcome.APPLIED, BulkSyncResult.Outcome.APPLIED, BulkSyncResult.Outcome.NO_CHANGES,
                BulkSyncResult.Outcome.SKIPPED, BulkSyncResult.Outcome.FAILED);
        assertThat(result.entries().get(0).inserted()).isEqualTo(1);
        assertThat(result.entries().get(1).inserted()).isEqualTo(1);
        assertThat(result.entries().get(1).deactivated()).isEqualTo(4);
    }

    @Test
    void sync_never_mutates_the_batch_kept_in_session() {
        Debt fresh = debt("New TV", "h-new", 1, 12);
        BulkStatementItem update = new BulkStatementItem("visa.pdf", VISA, BulkItemAction.UPDATE);
        update.setDiff(diff(VISA, List.of(new DebtDiff(fresh, null, DebtDiffStatus.NEW)), List.of(), List.of(), List.of()));
        BulkStatementBatch batch = new BulkStatementBatch(new ArrayList<>(List.of(update)));

        service.sync(batch);

        ArgumentCaptor<StatementDiffResult> captured = ArgumentCaptor.forClass(StatementDiffResult.class);
        verify(syncUseCase).sync(captured.capture(), eq(SyncMode.UPDATE));
        assertThat(captured.getValue()).isNotSameAs(update.getDiff());
        verify(previewUseCase, never()).recompute(any(), any());
    }
}
