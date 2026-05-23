package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web.view;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FindAllFixedExpenseCatalogsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FindAllFixedExpenseUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.FixedExpenseCatalogRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.FixedExpenseRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.FixedExpenseCatalogMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.CreateFixedExpenseCatalogReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.FixedExpenseReq;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/fixed-expenses")
public class FixedExpenseViewController {

    private final FixedExpenseRepository fixedExpenseRepository;
    private final FixedExpenseCatalogRepository fixedExpenseCatalogRepository;
    private final FixedExpenseCatalogMapper fixedExpenseCatalogMapper;
    private final FindAllFixedExpenseUseCase findAllFixedExpenseUseCase;
    private final FindAllFixedExpenseCatalogsUseCase findAllFixedExpenseCatalogsUseCase;

    public FixedExpenseViewController(
            FixedExpenseRepository fixedExpenseRepository,
            FixedExpenseCatalogRepository fixedExpenseCatalogRepository,
            FixedExpenseCatalogMapper fixedExpenseCatalogMapper,
            FindAllFixedExpenseUseCase findAllFixedExpenseUseCase,
            FindAllFixedExpenseCatalogsUseCase findAllFixedExpenseCatalogsUseCase) {
        this.fixedExpenseRepository = fixedExpenseRepository;
        this.fixedExpenseCatalogRepository = fixedExpenseCatalogRepository;
        this.fixedExpenseCatalogMapper = fixedExpenseCatalogMapper;
        this.findAllFixedExpenseUseCase = findAllFixedExpenseUseCase;
        this.findAllFixedExpenseCatalogsUseCase = findAllFixedExpenseCatalogsUseCase;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/ui";
        model.addAttribute("expenses", findAllFixedExpenseUseCase.getByEmail(email));
        model.addAttribute("catalogs", findAllFixedExpenseCatalogsUseCase.getCatalogList());
        model.addAttribute("newExpense", new FixedExpenseReq());
        model.addAttribute("newCatalog", new CreateFixedExpenseCatalogReq());
        model.addAttribute("email", email);
        return "fixed-expenses/list";
    }

    @PostMapping
    public String createExpense(@ModelAttribute FixedExpenseReq req, HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return "redirect:/ui";
        req.setUserEmail(email);
        fixedExpenseRepository.save(req, req.getCatalogId());
        return "redirect:/ui/fixed-expenses";
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(@PathVariable Integer id) {
        fixedExpenseRepository.delete(id);
        return "redirect:/ui/fixed-expenses";
    }

    @PostMapping("/catalog")
    public String createCatalog(@ModelAttribute CreateFixedExpenseCatalogReq req) {
        fixedExpenseCatalogRepository.save(fixedExpenseCatalogMapper.toModel(req));
        return "redirect:/ui/fixed-expenses";
    }

    @DeleteMapping("/catalog/{id}")
    public String deleteCatalog(@PathVariable Integer id) {
        fixedExpenseCatalogRepository.delete(id);
        return "redirect:/ui/fixed-expenses";
    }
}
