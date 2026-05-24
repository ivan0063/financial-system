package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtDuplicationPreventUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.ExtractFromFileUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FilterDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FindAllDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.LoadDebtList;
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
    private final DebtMapper debtMapper;
    private final DebtAccountRepository debtAccountRepository;
    private final FindAllDebtsUseCase findAllDebtsUseCase;
    private final ActivityLogHelper activityLogHelper;

    public AccountStatementViewController(
            ExtractFromFileUseCase extractFromFileUseCase,
            FilterDebtsUseCase filterDebtsUseCase,
            DebtDuplicationPreventUseCase debtDuplicationPreventUseCase,
            LoadDebtList loadDebtList,
            DebtMapper debtMapper,
            DebtAccountRepository debtAccountRepository,
            FindAllDebtsUseCase findAllDebtsUseCase,
            ActivityLogHelper activityLogHelper) {
        this.extractFromFileUseCase = extractFromFileUseCase;
        this.filterDebtsUseCase = filterDebtsUseCase;
        this.debtDuplicationPreventUseCase = debtDuplicationPreventUseCase;
        this.loadDebtList = loadDebtList;
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

    @PostMapping("/{debtAccountCode}/extract")
    public String extract(
            @PathVariable String debtAccountCode,
            @RequestParam("file") MultipartFile file,
            @RequestParam AccountStatementType accountStatementType,
            HttpSession session) throws IOException {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        List<Debt> debts = extractFromFileUseCase.extractDebts(file, debtAccountCode, accountStatementType)
                .stream()
                .peek(d -> d.setHashSum(debtDuplicationPreventUseCase.getHashSum(d, debtAccountCode)))
                .toList();
        AccountStatementPreviewDto preview = filterDebtsUseCase.previewAccountStatement(debts, debtAccountCode);
        session.setAttribute("statementPreview", preview);
        activityLogHelper.log(session, "Extract — " + debtAccountCode, preview);
        return "redirect:/ui/statements/" + debtAccountCode + "/preview";
    }

    @GetMapping("/{debtAccountCode}/preview")
    public String preview(@PathVariable String debtAccountCode, HttpSession session, Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        AccountStatementPreviewDto preview =
                (AccountStatementPreviewDto) session.getAttribute("statementPreview");
        if (preview == null) return "redirect:/ui/statements/" + debtAccountCode;
        model.addAttribute("currentDebts", findAllDebtsUseCase.getActiveByDebtAccount(debtAccountCode));
        model.addAttribute("newDebts", preview.newDebts());
        model.addAttribute("installmentUpdates", preview.installmentUpdates());
        model.addAttribute("debtTypes", DebtTypeEnum.values());
        model.addAttribute("debtAccountCode", debtAccountCode);
        model.addAttribute("form", new DebtListForm());
        return "statements/preview";
    }

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
        session.removeAttribute("statementPreview");
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }

    @PostMapping("/{debtAccountCode}/sync")
    public String sync(
            @PathVariable String debtAccountCode,
            @RequestParam("file") MultipartFile file,
            @RequestParam AccountStatementType accountStatementType,
            HttpSession session) throws IOException {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        List<Debt> debts = extractFromFileUseCase.extractDebts(file, debtAccountCode, accountStatementType)
                .stream()
                .peek(d -> d.setHashSum(debtDuplicationPreventUseCase.getHashSum(d, debtAccountCode)))
                .toList();
        filterDebtsUseCase.deactivateObsoleteDebts(debts, debtAccountCode);
        List<Debt> saved = loadDebtList.saveUnrepeated(debts, debtAccountCode);
        activityLogHelper.log(session, "Full Sync — " + debtAccountCode, saved);
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }
}
