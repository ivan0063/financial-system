package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.GetDebtProgressionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.CardProgressionDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.MonthProgressionDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DebtProgressionService implements GetDebtProgressionUseCase {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "MX"));

    private final DebtRepository debtRepository;

    public DebtProgressionService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    @Override
    public List<MonthProgressionDto> getProgression(String email, YearMonth targetMonth) {
        List<Debt> allDebts = debtRepository.findAllDebtsByUser(email);

        Map<DebtAccount, List<Debt>> byAccount = new LinkedHashMap<>();
        for (Debt debt : allDebts) {
            byAccount.computeIfAbsent(debt.getDebtAccount(), k -> new ArrayList<>()).add(debt);
        }

        YearMonth current = YearMonth.now();
        List<MonthProgressionDto> result = new ArrayList<>();

        for (YearMonth month = current.plusMonths(1); !month.isAfter(targetMonth); month = month.plusMonths(1)) {
            long offset = current.until(month, java.time.temporal.ChronoUnit.MONTHS);

            List<CardProgressionDto> cards = new ArrayList<>();
            BigDecimal totalPayment = BigDecimal.ZERO;

            for (Map.Entry<DebtAccount, List<Debt>> entry : byAccount.entrySet()) {
                DebtAccount account = entry.getKey();
                List<Debt> debts = entry.getValue();

                List<Debt> closingDebts = debts.stream()
                        .filter(d -> (d.getMaxFinancingTerm() - d.getCurrentInstallment()) == offset)
                        .toList();

                BigDecimal cardPayment = debts.stream()
                        .filter(d -> (d.getMaxFinancingTerm() - d.getCurrentInstallment()) >= offset)
                        .map(Debt::getMonthlyPayment)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (cardPayment.compareTo(BigDecimal.ZERO) > 0) {
                    cards.add(new CardProgressionDto(account.getCode(), account.getName(), closingDebts, cardPayment));
                    totalPayment = totalPayment.add(cardPayment);
                }
            }

            String displayName = month.format(DISPLAY_FORMAT);
            result.add(new MonthProgressionDto(month, displayName, totalPayment, cards));
        }

        return result;
    }
}
