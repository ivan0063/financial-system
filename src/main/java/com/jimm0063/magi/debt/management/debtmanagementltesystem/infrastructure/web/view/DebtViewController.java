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

@Controller
@RequestMapping("/ui/debts")
public class DebtViewController {

    private final DebtRepository debtRepository;
    private final DebtMapper debtMapper;
    private final PayOffDebtAccountUseCase payOffDebtAccountUseCase;

    public DebtViewController(DebtRepository debtRepository,
                              DebtMapper debtMapper,
                              PayOffDebtAccountUseCase payOffDebtAccountUseCase) {
        this.debtRepository = debtRepository;
        this.debtMapper = debtMapper;
        this.payOffDebtAccountUseCase = payOffDebtAccountUseCase;
    }

    @PostMapping("/{debtAccountCode}")
    public String createDebt(@ModelAttribute CreateDebtReq req,
                             @PathVariable String debtAccountCode,
                             HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        debtRepository.save(debtMapper.toModel(req), debtAccountCode);
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }

    @DeleteMapping("/{debtId}")
    public String deleteDebt(@PathVariable Integer debtId,
                             @RequestParam String debtAccountCode) {
        debtRepository.delete(debtId);
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }

    @PostMapping("/{debtAccountCode}/pay-off")
    public String payOffDebts(@PathVariable String debtAccountCode, HttpSession session) {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        payOffDebtAccountUseCase.payOffByDebtAccountCode(debtAccountCode);
        return "redirect:/ui/debt-accounts/" + debtAccountCode;
    }
}
