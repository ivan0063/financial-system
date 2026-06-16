package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.IgnorableDebt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.IgnorableDebtEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IgnorableDebtMapper {
    IgnorableDebtEntity toEntity(IgnorableDebt ignorableDebt);
    IgnorableDebt toModel(IgnorableDebtEntity entity);
}
