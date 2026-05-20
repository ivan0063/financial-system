package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.*;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtAccountRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.EntityNotFoundException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.utils.DebtComparatorUtil;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DebtService implements FilterDebtsUseCase, PayOffDebtAccountUseCase, FindAllDebtsUseCase, LoadDebtList, DebtDuplicationPreventUseCase {
    private final DebtRepository debtRepository;
    private final DebtAccountRepository debtAccountRepository;

    public DebtService(DebtRepository debtRepository, DebtAccountRepository debtAccountRepository) {
        this.debtRepository = debtRepository;
        this.debtAccountRepository = debtAccountRepository;
    }

    @Override
    public List<Debt> filterAccountStatementDebts(List<Debt> accountStatementDebts, String debtAccountCode) {
        List<Debt> debtAccountDebts = this.debtRepository.findAllDebtsByDebtAccountAndActiveTrue(debtAccountCode)
                .stream()
                .peek(debt -> {
                    if (debt.getHashSum() == null) {
                        debt.setHashSum(this.getHashSum(debt, debtAccountCode));
                    }
                })
                .toList();
        return DebtComparatorUtil.filterAccountStatementDebts(debtAccountDebts, accountStatementDebts);
    }

    @Override
    public void deactivateObsoleteDebts(List<Debt> statementDebts, String debtAccountCode) {
        List<Debt> dbDebts = this.debtRepository.findAllDebtsByDebtAccountAndActiveTrue(debtAccountCode)
                .stream()
                .peek(d -> { if (d.getHashSum() == null) d.setHashSum(this.getHashSum(d, debtAccountCode)); })
                .toList();

        if (dbDebts.isEmpty()) return;

        Set<String> activeStatementHashes = statementDebts.stream()
                .filter(d -> !d.getCurrentInstallment().equals(d.getMaxFinancingTerm()))
                .map(Debt::getHashSum)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Debt> toDeactivate = dbDebts.stream()
                .filter(d -> !activeStatementHashes.contains(d.getHashSum()))
                .peek(d -> d.setActive(false))
                .toList();

        if (!toDeactivate.isEmpty()) {
            this.debtRepository.saveAll(toDeactivate);
        }
    }

    @Override
    public List<Debt> payOffByDebtAccountCode(String debtAccountCode) {
        List<Debt> debtsToPayOff = this.debtRepository.findAllDebtsByDebtAccountAndActiveTrue(debtAccountCode)
                .stream()
                .peek(debt -> debt.setActive(false))
                .toList();

        this.debtRepository.saveAll(debtsToPayOff);
        return debtsToPayOff;
    }

    @Override
    public List<Debt> getActiveByDebtAccount(String debtAccountCode) {
        return debtRepository.findAllDebtsByDebtAccountAndActiveTrue(debtAccountCode);
    }

    @Override
    public List<Debt> saveUnrepeated(List<Debt> debts, String debtAccountCode) {
        DebtAccount debtAccount = this.debtAccountRepository.findDebtAccountByCodeAndActiveTrue(debtAccountCode)
                .orElseThrow(() -> new EntityNotFoundException("Debt Account " + debtAccountCode));

        List<Debt> debtAccountDebts = debtRepository.findAllDebtsByDebtAccountAndActiveTrue(debtAccountCode)
                .stream()
                .peek(d -> { if (d.getHashSum() == null) d.setHashSum(this.getHashSum(d, debtAccountCode)); })
                .toList();

        debts.stream()
                .filter(debt -> Objects.isNull(debt.getHashSum()))
                .forEach(debt -> debt.setHashSum(this.getHashSum(debt, debtAccountCode)));

        Map<String, Debt> dbByHash = debtAccountDebts.stream()
                .filter(d -> d.getHashSum() != null)
                .collect(Collectors.toMap(Debt::getHashSum, d -> d));

        List<Debt> toSave = new ArrayList<>();
        List<Debt> toUpdate = new ArrayList<>();

        for (Debt debt : DebtComparatorUtil.filterAccountStatementDebts(debtAccountDebts, debts)) {
            Debt dbMatch = dbByHash.get(debt.getHashSum());
            if (dbMatch != null) {
                dbMatch.setCurrentInstallment(debt.getCurrentInstallment());
                toUpdate.add(dbMatch);
            } else {
                debt.setDebtAccount(debtAccount);
                toSave.add(debt);
            }
        }

        if (!toUpdate.isEmpty()) debtRepository.saveAll(toUpdate);
        return debtRepository.saveAll(toSave);
    }

    @Override
    public String getHashSum(Debt debt, String debtAccountCode) {
        String monthAmountTrim = debt.getMonthlyPayment().setScale(2, RoundingMode.UNNECESSARY)
                .movePointRight(2)
                .toPlainString();

        String toHash = String.join("|",
                debtAccountCode.trim(),
                monthAmountTrim,
                debt.getMaxFinancingTerm().toString()
        );

        try {
            // Get an instance of the SHA-256 algorithm
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Convert the input string to bytes using UTF-8 encoding
            byte[] hashBytes = md.digest(toHash.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a BigInteger for easy hexadecimal conversion
            BigInteger no = new BigInteger(1, hashBytes);

            // Convert the BigInteger to a hexadecimal string
            StringBuilder hashedPayload = new StringBuilder(no.toString(16));

            // Pad with leading zeros to ensure the correct length (64 characters for SHA-256)
            while (hashedPayload.length() < 64) {
                hashedPayload.insert(0, "0");
            }

            return hashedPayload.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
