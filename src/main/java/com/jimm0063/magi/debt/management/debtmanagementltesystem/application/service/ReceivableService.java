package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FindAllReceivablesUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.GetReceivableStatusUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.RecordReceivablePaymentUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.ReceivablePaymentRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.ReceivableRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.ReceivableStatusDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.EntityNotFoundException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Receivable;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.ReceivablePayment;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.ReceivablePaymentReq;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ReceivableService implements FindAllReceivablesUseCase, RecordReceivablePaymentUseCase, GetReceivableStatusUseCase {
    private final ReceivableRepository receivableRepository;
    private final ReceivablePaymentRepository receivablePaymentRepository;

    public ReceivableService(ReceivableRepository receivableRepository, ReceivablePaymentRepository receivablePaymentRepository) {
        this.receivableRepository = receivableRepository;
        this.receivablePaymentRepository = receivablePaymentRepository;
    }

    @Override
    public List<Receivable> getByEmail(String email) {
        return receivableRepository.findAllByUser(email);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ReceivableStatusDto registerPayment(Integer receivableId, ReceivablePaymentReq receivablePaymentReq) {
        Receivable receivable = receivableRepository.findById(receivableId)
                .orElseThrow(() -> new EntityNotFoundException("Receivable " + receivableId + " not found"));

        ReceivablePayment payment = new ReceivablePayment();
        payment.setAmount(receivablePaymentReq.getAmount());
        payment.setNote(receivablePaymentReq.getNote());
        payment.setReceivable(receivable);
        receivablePaymentRepository.save(payment);

        return buildStatus(receivable);
    }

    @Override
    public ReceivableStatusDto getStatus(Integer receivableId) {
        Receivable receivable = receivableRepository.findById(receivableId)
                .orElseThrow(() -> new EntityNotFoundException("Receivable " + receivableId + " not found"));
        return buildStatus(receivable);
    }

    @Override
    public List<ReceivableStatusDto> getAllStatusesByEmail(String email) {
        return receivableRepository.findAllByUser(email).stream()
                .map(this::buildStatus)
                .toList();
    }

    private ReceivableStatusDto buildStatus(Receivable receivable) {
        List<ReceivablePayment> payments = receivablePaymentRepository.findAllByReceivableId(receivable.getId());

        BigDecimal totalRepaid = payments.stream()
                .map(ReceivablePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingAmount = receivable.getPrincipalAmount().subtract(totalRepaid);
        boolean fullyPaid = pendingAmount.compareTo(BigDecimal.ZERO) <= 0;

        // Dynamically re-open/close the receivable as payments move it across the fully-paid threshold
        if (fullyPaid != !receivable.getActive()) {
            receivable.setActive(!fullyPaid);
            receivable = receivableRepository.update(receivable);
        }

        double percentPaid = receivable.getPrincipalAmount().compareTo(BigDecimal.ZERO) == 0
                ? 100.0
                : Math.min(totalRepaid.divide(receivable.getPrincipalAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100, 100.0);

        ReceivableStatusDto dto = new ReceivableStatusDto();
        dto.setReceivable(receivable);
        dto.setTotalRepaid(totalRepaid);
        dto.setPendingAmount(pendingAmount.max(BigDecimal.ZERO));
        dto.setFullyPaid(fullyPaid);
        dto.setPercentPaid(Math.round(percentPaid * 100.0) / 100.0);
        dto.setPayments(payments);
        return dto;
    }
}
