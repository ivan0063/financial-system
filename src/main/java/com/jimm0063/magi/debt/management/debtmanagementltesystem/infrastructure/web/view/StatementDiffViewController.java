package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.PreviewStatementDiffUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.SyncStatementDiffUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtAccountRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.SyncMode;
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

@Controller
@RequestMapping("/ui/v2/statements")
public class StatementDiffViewController {

    private static final String SESSION_KEY = "statementDiff";

    private final PreviewStatementDiffUseCase previewUseCase;
    private final SyncStatementDiffUseCase syncUseCase;
    private final DebtAccountRepository debtAccountRepository;
    private final ActivityLogHelper activityLogHelper;

    public StatementDiffViewController(
            PreviewStatementDiffUseCase previewUseCase,
            SyncStatementDiffUseCase syncUseCase,
            DebtAccountRepository debtAccountRepository,
            ActivityLogHelper activityLogHelper) {
        this.previewUseCase = previewUseCase;
        this.syncUseCase = syncUseCase;
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

    /** Step 1 — parse the file, compute the diff, store it in session, redirect to preview. */
    @PostMapping("/{debtAccountCode}/extract")
    public String extract(
            @PathVariable String debtAccountCode,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        try {
            StatementDiffResult diff = previewUseCase.preview(file, debtAccountCode);
            session.setAttribute(SESSION_KEY, diff);
            activityLogHelper.log(session, "Statement diff preview — " + debtAccountCode, diff);
            return "redirect:/ui/v2/statements/" + debtAccountCode + "/preview";
        } catch (IOException e) {
            return "redirect:/ui/v2/statements/" + debtAccountCode + "?error=parse_failed";
        } catch (IllegalArgumentException e) {
            return "redirect:/ui/v2/statements/" + debtAccountCode + "?error=missing_fields";
        }
    }

    /** Step 2 — show the diff so the user can decide how to apply it. */
    @GetMapping("/{debtAccountCode}/preview")
    public String preview(
            @PathVariable String debtAccountCode,
            HttpSession session,
            Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        Object attr = session.getAttribute(SESSION_KEY);
        if (!(attr instanceof StatementDiffResult diff))
            return "redirect:/ui/v2/statements/" + debtAccountCode;
        model.addAttribute("diff", diff);
        model.addAttribute("debtAccountCode", debtAccountCode);
        return "v2/statements/preview";
    }

    /** Step 3 — apply the chosen sync mode and redirect to the account detail page. */
    @PostMapping("/{debtAccountCode}/sync")
    public String sync(
            @PathVariable String debtAccountCode,
            @RequestParam SyncMode mode,
            HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        Object attr = session.getAttribute(SESSION_KEY);
        if (!(attr instanceof StatementDiffResult diff))
            return "redirect:/ui/v2/statements/" + debtAccountCode;
        try {
            syncUseCase.sync(diff, mode);
            activityLogHelper.log(session, "Statement sync [" + mode + "] — " + debtAccountCode, diff);
            session.removeAttribute(SESSION_KEY);
            return "redirect:/ui/debt-accounts/" + debtAccountCode;
        } catch (Exception e) {
            return "redirect:/ui/v2/statements/" + debtAccountCode + "/preview?error=sync_failed";
        }
    }
}
