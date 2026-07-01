package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Receivable;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.ReceivableEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.CreateReceivableReq;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReceivableMapper {
    ReceivableEntity toEntity(Receivable receivable);
    Receivable toModel(ReceivableEntity receivableEntity);
    Receivable toModel(CreateReceivableReq createReceivableReq);
}
