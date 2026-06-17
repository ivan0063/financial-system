package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.GetIgnorableDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.MarkDebtAsIgnorableUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.RemoveIgnorableDebtUseCase;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/ui/ignorable-debts")
public class IgnorableDebtViewController {

    private final GetIgnorableDebtsUseCase getIgnorableDebtsUseCase;
    private final MarkDebtAsIgnorableUseCase markDebtAsIgnorableUseCase;
    private final RemoveIgnorableDebtUseCase removeIgnorableDebtUseCase;
    private final ActivityLogHelper activityLogHelper;

    public IgnorableDebtViewController(GetIgnorableDebtsUseCase getIgnorableDebtsUseCase,
                                        MarkDebtAsIgnorableUseCase markDebtAsIgnorableUseCase,
                                        RemoveIgnorableDebtUseCase removeIgnorableDebtUseCase,
                                        ActivityLogHelper activityLogHelper) {
        this.getIgnorableDebtsUseCase = getIgnorableDebtsUseCase;
        this.markDebtAsIgnorableUseCase = markDebtAsIgnorableUseCase;
        this.removeIgnorableDebtUseCase = removeIgnorableDebtUseCase;
        this.activityLogHelper = activityLogHelper;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        model.addAttribute("ignorableDebts", getIgnorableDebtsUseCase.getAllIgnorableDebts());
        return "ignorable-debts/list";
    }

    @PostMapping
    public String add(@RequestParam String hashSum,
                      @RequestParam String reason,
                      HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        var saved = markDebtAsIgnorableUseCase.markAsIgnorable(hashSum.trim(), reason.trim());
        activityLogHelper.log(session, "Mark Debt Ignorable", saved);
        return "redirect:/ui/ignorable-debts";
    }

    @DeleteMapping("/{hashSum}")
    public String remove(@PathVariable String hashSum, HttpSession session) {
        removeIgnorableDebtUseCase.removeIgnore(hashSum);
        activityLogHelper.log(session, "Remove Ignorable Debt", Map.of("hashSum", hashSum));
        return "redirect:/ui/ignorable-debts";
    }
}
