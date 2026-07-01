package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.adapter;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.ReceivablePaymentRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.EntityNotFoundException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.ReceivablePayment;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.ReceivableEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.ReceivablePaymentEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.ReceivablePaymentMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.ReceivableJpaRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.ReceivablePaymentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReceivablePaymentRepositoryAdapter implements ReceivablePaymentRepository {
    private final ReceivablePaymentJpaRepository receivablePaymentJpaRepository;
    private final ReceivablePaymentMapper receivablePaymentMapper;
    private final ReceivableJpaRepository receivableJpaRepository;

    public ReceivablePaymentRepositoryAdapter(ReceivablePaymentJpaRepository receivablePaymentJpaRepository,
                                              ReceivablePaymentMapper receivablePaymentMapper,
                                              ReceivableJpaRepository receivableJpaRepository) {
        this.receivablePaymentJpaRepository = receivablePaymentJpaRepository;
        this.receivablePaymentMapper = receivablePaymentMapper;
        this.receivableJpaRepository = receivableJpaRepository;
    }

    @Override
    public ReceivablePayment save(ReceivablePayment receivablePayment) {
        ReceivableEntity receivableEntity = receivableJpaRepository.findById(receivablePayment.getReceivable().getId())
                .orElseThrow(() -> new EntityNotFoundException("Receivable " + receivablePayment.getReceivable().getId() + " not found"));
        ReceivablePaymentEntity receivablePaymentEntity = receivablePaymentMapper.toEntity(receivablePayment);
        receivablePaymentEntity.setReceivable(receivableEntity);
        return receivablePaymentMapper.toModel(receivablePaymentJpaRepository.save(receivablePaymentEntity));
    }

    @Override
    public List<ReceivablePayment> findAllByReceivableId(Integer receivableId) {
        return receivablePaymentJpaRepository.findAllByReceivable_IdOrderByCreatedAtDesc(receivableId)
                .stream()
                .map(receivablePaymentMapper::toModel)
                .toList();
    }
}
