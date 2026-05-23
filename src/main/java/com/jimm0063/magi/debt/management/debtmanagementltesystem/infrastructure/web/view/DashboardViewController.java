package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.GetFinancialStatusUseCase;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ui")
public class DashboardViewController {

    private final GetFinancialStatusUseCase getFinancialStatusUseCase;

    public DashboardViewController(GetFinancialStatusUseCase getFinancialStatusUseCase) {
        this.getFinancialStatusUseCase = getFinancialStatusUseCase;
    }

    @GetMapping
    public String home(HttpSession session) {
        if (session.getAttribute("userEmail") != null) {
            return "redirect:/ui/dashboard";
        }
        return "home";
    }

    @PostMapping("/session")
    public String setEmail(@RequestParam String email, HttpSession session) {
        session.setAttribute("userEmail", email);
        return "redirect:/ui/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/ui";
        model.addAttribute("dashboard", getFinancialStatusUseCase.getUserStatus(email));
        model.addAttribute("email", email);
        return "dashboard/index";
    }
}
