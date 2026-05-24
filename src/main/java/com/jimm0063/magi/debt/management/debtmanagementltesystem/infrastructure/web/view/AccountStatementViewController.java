package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtDuplicationPreventUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.ExtractFromFileUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FilterDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FindAllDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.LoadDebtList;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.SourceOfTruthImportUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtAccountRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.AccountStatementPreviewDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.AccountStatementType;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.DebtMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.DebtListForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/ui/statements")
public class AccountStatementViewController {

    private final ExtractFromFileUseCase extractFromFileUseCase;
    private final FilterDebtsUseCase filterDebtsUseCase;
    private final DebtDuplicationPreventUseCase debtDuplicationPreventUseCase;
    private final LoadDebtList loadDebtList;
    private final SourceOfTruthImportUseCase sourceOfTruthImportUseCase;
    private final DebtMapper debtMapper;
    private final DebtAccountRepository debtAccountRepository;
    private final FindAllDebtsUseCase findAllDebtsUseCase;
    private final ActivityLogHelper activityLogHelper;

    public AccountStatementViewController(
            ExtractFromFileUseCase extractFromFileUseCase,
            FilterDebtsUseCase filterDebtsUseCase,
            DebtDuplicationPreventUseCase debtDuplicationPreventUseCase,
            LoadDebtList loadDebtList,
            SourceOfTruthImportUseCase sourceOfTruthImportUseCase,
            DebtMapper debtMapper,
            DebtAccountRepository debtAccountRepository,
            FindAllDebtsUseCase findAllDebtsUseCase,
            ActivityLogHelper activityLogHelper) {
        this.extractFromFileUseCase = extractFromFileUseCase;
        this.filterDebtsUseCase = filterDebtsUseCase;
        this.debtDuplicationPreventUseCase = debtDuplicationPreventUseCase;
        this.loadDebtList = loadDebtList;
        this.sourceOfTruthImportUseCase = sourceOfTruthImportUseCase;
        this.debtMapper = debtMapper;
        this.debtAccountRepository = debtAccountRepository;
        this.findAllDebtsUseCase = findAllDebtsUseCase;
        this.activityLogHelper = activityLogHelper;
    }

    @GetMapping("/{debtAccountCode}")
    public String uploadForm(@PathVariable String debtAccountCode, HttpSession session, Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        model.addAttribute("account",
                debtAccountRepository.findDebtAccountByCodeAndActiveTrue(debtAccountCode).orElse(null));
        model.addAttribute("statementTypes", AccountStatementType.values());
        model.addAttribute("debtAccountCode", debtAccountCode);
        return "statements/upload";
    }

    /** Step 1 — parse the file, store raw debts + preview in session, redirect to preview. */
    @PostMapping("/{debtAccountCode}/extract")
    public String extract(
            @PathVariable String debtAccountCode,
            @RequestParam("file") MultipartFile file,
            @RequestParam AccountStatementType accountStatementType,
            HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        try {
            List<Debt> debts = extractFromFileUseCase.extractDebts(file, debtAccountCode, accountStatementType)
                    .stream()
                    .peek(d -> d.setHashSum(debtDuplicationPreventUseCase.getHashSum(d, debtAccountCode)))
                    .toList();
            AccountStatementPreviewDto preview = filterDebtsUseCase.previewAccountStatement(debts, debtAccountCode);
            session.setAttribute("extractedDebts", debts);
            session.setAttribute("statementPreview", preview);
            activityLogHelper.log(session, "Extract — " + debtAccountCode, preview);
            return "redirect:/ui/statements/" + debtAccountCode + "/preview";
        } catch (IOException e) {
            return "redirect:/ui/statements/" + debtAccountCode + "?error=parse_failed";
        } catch (IllegalArgumentException e) {
            return "redirect:/ui/statements/" + debtAccountCode + "?error=missing_fields";
        }
    }

    @GetMapping("/{debtAccountCode}/preview")
    public String preview(@PathVariable String debtAccountCode, HttpSession session, Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        if (!(session.getAttribute("statementPreview") instanceof AccountStatementPreviewDto preview))
            return "redirect:/ui/statements/" + debtAccountCode;
        model.addAttribute("currentDebts", findAllDebtsUseCase.getActiveByDebtAccount(debtAccountCode));
        model.addAttribute("newDebts", preview.newDebts());
        model.addAttribute("installmentUpdates", preview.installmentUpdates());
        model.addAttribute("completedDebts", preview.completedDebts());
        model.addAttribute("debtTypes", DebtTypeEnum.values());
        model.addAttribute("debtAccountCode", debtAccountCode);
        model.addAttribute("form", new DebtListForm());
        return "statements/preview";
    }

    /** Persist option A — save only new/selected debts (user-editable form). */
    @PostMapping("/{debtAccountCode}/add")
    public String add(
            @PathVariable String debtAccountCode,
            @ModelAttribute("form") DebtListForm form,
            HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        List<Debt> debts = form.getDebts().stream()
                .filter(req -> req != null
                        && req.getDescription() != null
                        && !req.getDescription().isBlank())
                .map(req -> {
                    Debt debt = debtMapper.toModel(req);
                    debt.setActive(true);
                    debt.setHashSum(debtDuplicationPreventUseCase.getHashSum(debt, debtAccountCode));
                    return debt;
                })
                .toList();
        List<Debt> saved = loadDebtList.saveUnrepeated(debts, debtAccountCode);
        activityLogHelper.log(session, "Add debts — " + debtAccountCode, saved);
        clearSession(session);
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }

    /** Persist option B — deactivate obsolete debts then save/update from extracted list. */
    @PostMapping("/{debtAccountCode}/sync")
    public String sync(@PathVariable String debtAccountCode, HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        List<Debt> debts = getExtractedDebts(session);
        if (debts == null) return "redirect:/ui/statements/" + debtAccountCode;
        filterDebtsUseCase.deactivateObsoleteDebts(debts, debtAccountCode);
        List<Debt> saved = loadDebtList.saveUnrepeated(debts, debtAccountCode);
        activityLogHelper.log(session, "Full Sync — " + debtAccountCode, saved);
        clearSession(session);
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }

    /** Persist option C — wipe all existing debts and import statement as source of truth. */
    @PostMapping("/{debtAccountCode}/replace")
    public String replace(@PathVariable String debtAccountCode, HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        List<Debt> debts = getExtractedDebts(session);
        if (debts == null) return "redirect:/ui/statements/" + debtAccountCode;
        List<Debt> saved = sourceOfTruthImportUseCase.replaceAllWithStatement(debts, debtAccountCode);
        activityLogHelper.log(session, "Source of Truth Replace — " + debtAccountCode, saved);
        clearSession(session);
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }

    @SuppressWarnings("unchecked")
    private List<Debt> getExtractedDebts(HttpSession session) {
        Object attr = session.getAttribute("extractedDebts");
        return (attr instanceof List<?>) ? (List<Debt>) attr : null;
    }

    private void clearSession(HttpSession session) {
        session.removeAttribute("extractedDebts");
        session.removeAttribute("statementPreview");
    }
}
