package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtAccountStatusUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FindAllDebtAccountUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FindAllDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtAccountRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.AccountStatementType;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.DebtAccountMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.CreateDebtAccountReq;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/ui/debt-accounts")
public class DebtAccountViewController {

    private final DebtAccountRepository debtAccountRepository;
    private final DebtAccountMapper debtAccountMapper;
    private final FindAllDebtAccountUseCase findAllDebtAccountUseCase;
    private final FindAllDebtsUseCase findAllDebtsUseCase;
    private final DebtAccountStatusUseCase debtAccountStatusUseCase;
    private final ActivityLogHelper activityLogHelper;

    public DebtAccountViewController(
            DebtAccountRepository debtAccountRepository,
            DebtAccountMapper debtAccountMapper,
            FindAllDebtAccountUseCase findAllDebtAccountUseCase,
            FindAllDebtsUseCase findAllDebtsUseCase,
            DebtAccountStatusUseCase debtAccountStatusUseCase,
            ActivityLogHelper activityLogHelper) {
        this.debtAccountRepository = debtAccountRepository;
        this.debtAccountMapper = debtAccountMapper;
        this.findAllDebtAccountUseCase = findAllDebtAccountUseCase;
        this.findAllDebtsUseCase = findAllDebtsUseCase;
        this.debtAccountStatusUseCase = debtAccountStatusUseCase;
        this.activityLogHelper = activityLogHelper;
    }

    @GetMapping
    public String list(@RequestParam String providerCode, HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/ui";
        model.addAttribute("accounts", findAllDebtAccountUseCase.getActiveByFinancialProvider(providerCode));
        model.addAttribute("providerCode", providerCode);
        model.addAttribute("newAccount", new CreateDebtAccountReq());
        model.addAttribute("statementTypes", AccountStatementType.values());
        model.addAttribute("email", email);
        return "debt-accounts/list";
    }

    @PostMapping("/{providerCode}")
    public String createAccount(@ModelAttribute CreateDebtAccountReq req,
                                @PathVariable String providerCode,
                                HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        var saved = debtAccountRepository.save(debtAccountMapper.toModel(req), providerCode);
        activityLogHelper.log(session, "Create Debt Account", saved);
        return "redirect:/ui/debt-accounts?providerCode=" + providerCode;
    }

    @DeleteMapping("/{code}")
    public String deleteAccount(@PathVariable String code,
                                @RequestParam(required = false) String providerCode,
                                HttpSession session) {
        debtAccountRepository.delete(code);
        activityLogHelper.log(session, "Delete Debt Account", Map.of("deleted", true, "code", code));
        return providerCode != null
                ? "redirect:/ui/debt-accounts?providerCode=" + providerCode
                : "redirect:/ui/dashboard";
    }

    @GetMapping("/{code}")
    public String detail(@PathVariable String code, HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/ui";
        model.addAttribute("account", debtAccountRepository.findDebtAccountByCodeAndActiveTrue(code).orElse(null));
        model.addAttribute("debts", findAllDebtsUseCase.getActiveByDebtAccount(code));
        model.addAttribute("status", debtAccountStatusUseCase.getStatus(code));
        model.addAttribute("debtAccountCode", code);
        model.addAttribute("debtTypes", DebtTypeEnum.values());
        model.addAttribute("email", email);
        return "debt-accounts/detail";
    }
}
