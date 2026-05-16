package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeck;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgDeckEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TcgDeckMapper {
    TcgDeckEntity toEntity(TcgDeck model);
    TcgDeck toModel(TcgDeckEntity entity);
}
