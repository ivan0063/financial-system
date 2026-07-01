package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.adapter;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.ReceivableRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.EntityNotFoundException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Receivable;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.DebtSysUserEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.ReceivableEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.ReceivableMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.DebtSysUserJpaRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.ReceivableJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReceivableRepositoryAdapter implements ReceivableRepository {
    private final ReceivableJpaRepository receivableJpaRepository;
    private final ReceivableMapper receivableMapper;
    private final DebtSysUserJpaRepository debtSysUserJpaRepository;

    public ReceivableRepositoryAdapter(ReceivableJpaRepository receivableJpaRepository, ReceivableMapper receivableMapper,
                                       DebtSysUserJpaRepository debtSysUserJpaRepository) {
        this.receivableJpaRepository = receivableJpaRepository;
        this.receivableMapper = receivableMapper;
        this.debtSysUserJpaRepository = debtSysUserJpaRepository;
    }

    @Override
    public List<Receivable> findAllByUser(String email) {
        return receivableJpaRepository.findAllByDebtSysUser_Email(email)
                .stream()
                .map(receivableMapper::toModel)
                .toList();
    }

    @Override
    public List<Receivable> findAllActiveByUser(String email) {
        return receivableJpaRepository.findAllByDebtSysUser_EmailAndActiveTrue(email)
                .stream()
                .map(receivableMapper::toModel)
                .toList();
    }

    @Override
    public Optional<Receivable> findById(Integer id) {
        return receivableJpaRepository.findById(id).map(receivableMapper::toModel);
    }

    @Override
    public Receivable save(Receivable receivable, String userEmail) {
        DebtSysUserEntity user = debtSysUserJpaRepository.findByEmailAndActiveTrue(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User " + userEmail + " not found"));
        ReceivableEntity receivableEntity = receivableMapper.toEntity(receivable);
        receivableEntity.setDebtSysUser(user);
        receivableEntity.setActive(true);
        return receivableMapper.toModel(receivableJpaRepository.save(receivableEntity));
    }

    @Override
    public Receivable update(Receivable receivable) {
        ReceivableEntity existing = receivableJpaRepository.findById(receivable.getId())
                .orElseThrow(() -> new EntityNotFoundException("Receivable " + receivable.getId() + " not found"));
        existing.setDebtorName(receivable.getDebtorName());
        existing.setDescription(receivable.getDescription());
        existing.setPrincipalAmount(receivable.getPrincipalAmount());
        existing.setLentDate(receivable.getLentDate());
        existing.setActive(receivable.getActive());
        return receivableMapper.toModel(receivableJpaRepository.save(existing));
    }

    @Override
    public void delete(Integer receivableId) {
        ReceivableEntity receivableEntity = receivableJpaRepository.findById(receivableId)
                .orElseThrow(() -> new EntityNotFoundException("Receivable " + receivableId + " not found"));
        receivableEntity.setActive(false);
        this.receivableJpaRepository.save(receivableEntity);
    }
}
