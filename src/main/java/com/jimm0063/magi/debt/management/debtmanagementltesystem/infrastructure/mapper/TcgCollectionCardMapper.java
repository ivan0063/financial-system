package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollectionCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgCollectionCardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TcgCollectionCardMapper {
    @Mapping(source = "collection.id", target = "collectionId")
    TcgCollectionCard toModel(TcgCollectionCardEntity entity);
}
