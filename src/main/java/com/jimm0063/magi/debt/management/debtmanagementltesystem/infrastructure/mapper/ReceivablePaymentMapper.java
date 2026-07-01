package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.ReceivablePayment;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.ReceivablePaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReceivablePaymentMapper {
    ReceivablePaymentEntity toEntity(ReceivablePayment receivablePayment);
    ReceivablePayment toModel(ReceivablePaymentEntity receivablePaymentEntity);
}
