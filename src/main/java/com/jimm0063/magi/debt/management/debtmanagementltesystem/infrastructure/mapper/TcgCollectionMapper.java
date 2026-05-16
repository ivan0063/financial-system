package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollection;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgCollectionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TcgCollectionMapper {
    TcgCollectionEntity toEntity(TcgCollection model);
    TcgCollection toModel(TcgCollectionEntity entity);
}
