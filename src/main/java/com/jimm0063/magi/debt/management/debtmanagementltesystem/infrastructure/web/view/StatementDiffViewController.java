package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.MarkDebtAsIgnorableUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.PreviewStatementDiffUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.SyncStatementDiffUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtAccountRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.SyncMode;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.StatementDiffResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/ui/v2/statements")
public class StatementDiffViewController {

    private static final String DIFF_KEY = "v2StatementDiff";
    private static final String DEBTS_KEY = "v2ExtractedDebts";

    private final PreviewStatementDiffUseCase previewUseCase;
    private final SyncStatementDiffUseCase syncUseCase;
    private final MarkDebtAsIgnorableUseCase markIgnorableUseCase;
    private final DebtAccountRepository debtAccountRepository;
    private final ActivityLogHelper activityLogHelper;

    public StatementDiffViewController(
            PreviewStatementDiffUseCase previewUseCase,
            SyncStatementDiffUseCase syncUseCase,
            MarkDebtAsIgnorableUseCase markIgnorableUseCase,
            DebtAccountRepository debtAccountRepository,
            ActivityLogHelper activityLogHelper) {
        this.previewUseCase = previewUseCase;
        this.syncUseCase = syncUseCase;
        this.markIgnorableUseCase = markIgnorableUseCase;
        this.debtAccountRepository = debtAccountRepository;
        this.activityLogHelper = activityLogHelper;
    }

    /** Upload form — shows the account name and which parser is linked to it. */
    @GetMapping("/{debtAccountCode}")
    public String uploadForm(
            @PathVariable String debtAccountCode,
            HttpSession session,
            Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        model.addAttribute("account",
                debtAccountRepository.findDebtAccountByCodeAndActiveTrue(debtAccountCode).orElse(null));
        model.addAttribute("debtAccountCode", debtAccountCode);
        return "v2/statements/upload";
    }

    /** Step 1 — parse the file, compute the diff, store both in session, redirect to preview. */
    @PostMapping("/{debtAccountCode}/extract")
    public String extract(
            @PathVariable String debtAccountCode,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        try {
            StatementDiffResult diff = previewUseCase.preview(file, debtAccountCode);
            List<Debt> allExtracted = collectExtractedDebts(diff);
            session.setAttribute(DIFF_KEY, diff);
            session.setAttribute(DEBTS_KEY, allExtracted);
            activityLogHelper.log(session, "Statement diff preview — " + debtAccountCode, diff);
            return "redirect:/ui/v2/statements/" + debtAccountCode + "/preview";
        } catch (IOException e) {
            return "redirect:/ui/v2/statements/" + debtAccountCode + "?error=parse_failed";
        } catch (IllegalArgumentException e) {
            return "redirect:/ui/v2/statements/" + debtAccountCode + "?error=missing_fields";
        }
    }

    /** Step 2 — show the diff so the user can review and choose how to apply it. */
    @GetMapping("/{debtAccountCode}/preview")
    public String preview(
            @PathVariable String debtAccountCode,
            HttpSession session,
            Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        Object attr = session.getAttribute(DIFF_KEY);
        if (!(attr instanceof StatementDiffResult diff))
            return "redirect:/ui/v2/statements/" + debtAccountCode;
        model.addAttribute("diff", diff);
        model.addAttribute("debtAccountCode", debtAccountCode);
        return "v2/statements/preview";
    }

    /**
     * Mark a debt as ignorable and recompute the diff immediately so the
     * debt moves to the IGNORED section without re-uploading the file.
     */
    @PostMapping("/{debtAccountCode}/mark-ignorable")
    public String markIgnorable(
            @PathVariable String debtAccountCode,
            @RequestParam String hashSum,
            @RequestParam String reason,
            HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";

        markIgnorableUseCase.markAsIgnorable(hashSum, reason);

        List<Debt> storedDebts = getStoredDebts(session);
        if (storedDebts != null) {
            StatementDiffResult updated = previewUseCase.recompute(storedDebts, debtAccountCode);
            session.setAttribute(DIFF_KEY, updated);
            activityLogHelper.log(session, "Mark ignorable — " + debtAccountCode,
                    "hash=" + hashSum + ", reason=" + reason);
        }
        return "redirect:/ui/v2/statements/" + debtAccountCode + "/preview";
    }

    /** Step 3 — apply the chosen sync mode and redirect to the account detail page. */
    @PostMapping("/{debtAccountCode}/sync")
    public String sync(
            @PathVariable String debtAccountCode,
            @RequestParam SyncMode mode,
            HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        Object attr = session.getAttribute(DIFF_KEY);
        if (!(attr instanceof StatementDiffResult diff))
            return "redirect:/ui/v2/statements/" + debtAccountCode;
        try {
            syncUseCase.sync(diff, mode);
            activityLogHelper.log(session, "Statement sync [" + mode + "] — " + debtAccountCode, diff);
            clearSession(session);
            return "redirect:/ui/debt-accounts/" + debtAccountCode;
        } catch (Exception e) {
            return "redirect:/ui/v2/statements/" + debtAccountCode + "/preview?error=sync_failed";
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Collects all extracted debts from every category in the diff so they can
     * be stored in session and reused when re-computing after a mark-ignorable action.
     */
    private List<Debt> collectExtractedDebts(StatementDiffResult diff) {
        return java.util.stream.Stream.of(
                        diff.newDebts(), diff.updatedDebts(),
                        diff.completedDebts(), diff.unchangedDebts(), diff.ignoredDebts())
                .flatMap(List::stream)
                .map(d -> d.getExtractedDebt())
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<Debt> getStoredDebts(HttpSession session) {
        Object attr = session.getAttribute(DEBTS_KEY);
        return (attr instanceof List<?>) ? (List<Debt>) attr : null;
    }

    private void clearSession(HttpSession session) {
        session.removeAttribute(DIFF_KEY);
        session.removeAttribute(DEBTS_KEY);
    }
}
