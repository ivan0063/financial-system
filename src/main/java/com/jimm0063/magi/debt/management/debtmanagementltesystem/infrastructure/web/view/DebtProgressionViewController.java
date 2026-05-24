package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.GetDebtProgressionUseCase;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;

@Controller
@RequestMapping("/ui/progression")
public class DebtProgressionViewController {

    private final GetDebtProgressionUseCase getDebtProgressionUseCase;

    public DebtProgressionViewController(GetDebtProgressionUseCase getDebtProgressionUseCase) {
        this.getDebtProgressionUseCase = getDebtProgressionUseCase;
    }

    @GetMapping
    public String progression(
            @RequestParam(required = false) String targetMonth,
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/ui";

        model.addAttribute("email", email);
        model.addAttribute("targetMonth", targetMonth);

        if (targetMonth != null && !targetMonth.isBlank()) {
            YearMonth target = YearMonth.parse(targetMonth);
            model.addAttribute("progression", getDebtProgressionUseCase.getProgression(email, target));
        }

        return "debt-progression/index";
    }
}
