package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DoPayment;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.PaymentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/ui/payments")
public class PaymentViewController {

    private final DoPayment doPayment;
    private final PaymentRepository paymentRepository;

    public PaymentViewController(DoPayment doPayment, PaymentRepository paymentRepository) {
        this.doPayment = doPayment;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/{debtAccountCode}")
    public String list(@PathVariable String debtAccountCode, HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/ui";
        model.addAttribute("payments", paymentRepository.findByDebtAccountCode(debtAccountCode));
        model.addAttribute("debtAccountCode", debtAccountCode);
        model.addAttribute("email", email);
        return "payments/list";
    }

    @PostMapping("/{debtAccountCode}")
    public String doPaymentAction(@PathVariable String debtAccountCode, HttpSession session) throws IOException {
        if (session.getAttribute("userEmail") == null) return "redirect:/ui";
        doPayment.cardPayment(debtAccountCode);
        return "redirect:/ui/payments/" + debtAccountCode;
    }
}
