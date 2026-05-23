package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.PayOffDebtAccountUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.DebtMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.CreateDebtReq;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/ui/debts")
public class DebtViewController {

    private final DebtRepository debtRepository;
    private final DebtMapper debtMapper;
    private final PayOffDebtAccountUseCase payOffDebtAccountUseCase;
    private final ActivityLogHelper activityLogHelper;

    public DebtViewController(DebtRepository debtRepository,
                              DebtMapper debtMapper,
                              PayOffDebtAccountUseCase payOffDebtAccountUseCase,
                              ActivityLogHelper activityLogHelper) {
        this.debtRepository = debtRepository;
        this.debtMapper = debtMapper;
        this.payOffDebtAccountUseCase = payOffDebtAccountUseCase;
        this.activityLogHelper = activityLogHelper;
    }

    @PostMapping("/{debtAccountCode}")
    public String createDebt(@ModelAttribute CreateDebtReq req,
                             @PathVariable String debtAccountCode,
                             HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        var saved = debtRepository.save(debtMapper.toModel(req), debtAccountCode);
        activityLogHelper.log(session, "Create Debt", saved);
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }

    @DeleteMapping("/{debtId}")
    public String deleteDebt(@PathVariable Integer debtId,
                             @RequestParam String debtAccountCode,
                             HttpSession session) {
        debtRepository.delete(debtId);
        activityLogHelper.log(session, "Delete Debt", Map.of("deleted", true, "id", debtId));
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }

    @PostMapping("/{debtAccountCode}/pay-off")
    public String payOffDebts(@PathVariable String debtAccountCode, HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        var paid = payOffDebtAccountUseCase.payOffByDebtAccountCode(debtAccountCode);
        activityLogHelper.log(session, "Pay Off — " + debtAccountCode, paid);
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }
}
