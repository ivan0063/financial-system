package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.configuration;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.FixedExpenseCatalogRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.FixedExpenseCatalog;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CatalogDataLoader implements CommandLineRunner {

    private static final List<String> DEFAULT_FIXED_EXPENSE_CATEGORIES = List.of(
            "Rent",
            "Mortgage",
            "Bank Loan",
            "Personal Loan",
            "Student Loan",
            "Car Loan",
            "Credit Card Payment",
            "Electricity",
            "Water",
            "Gas",
            "Internet",
            "Mobile Phone",
            "Landline Phone",
            "Cable / Streaming Services",
            "Software Subscriptions",
            "Cloud Storage",
            "Gym Membership",
            "Health Insurance",
            "Life Insurance",
            "Auto Insurance",
            "Home Insurance",
            "Property Tax",
            "HOA Fees",
            "Childcare",
            "Education / Tuition",
            "Groceries",
            "Transportation",
            "Parking",
            "Pet Care",
            "Home Maintenance",
            "Legal Fees",
            "Professional Services",
            "Charity / Donations",
            "Savings Contribution",
            "Taxes",
            "Membership Dues",
            "Entertainment"
    );

    private final FixedExpenseCatalogRepository fixedExpenseCatalogRepository;

    public CatalogDataLoader(FixedExpenseCatalogRepository fixedExpenseCatalogRepository) {
        this.fixedExpenseCatalogRepository = fixedExpenseCatalogRepository;
    }

    @Override
    public void run(String... args) {
        Set<String> existingNames = fixedExpenseCatalogRepository.findAll().stream()
                .map(FixedExpenseCatalog::getName)
                .filter(Objects::nonNull)
                .map(name -> name.trim().toLowerCase())
                .collect(Collectors.toSet());

        DEFAULT_FIXED_EXPENSE_CATEGORIES.stream()
                .filter(category -> !existingNames.contains(category.toLowerCase()))
                .forEach(category -> {
                    FixedExpenseCatalog catalog = new FixedExpenseCatalog();
                    catalog.setName(category);
                    fixedExpenseCatalogRepository.save(catalog);
                });
    }
}
