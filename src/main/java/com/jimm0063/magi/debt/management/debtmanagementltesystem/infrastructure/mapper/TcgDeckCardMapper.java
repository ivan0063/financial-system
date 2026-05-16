package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeckCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgDeckCardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TcgDeckCardMapper {
    @Mapping(source = "deck.id", target = "deckId")
    TcgDeckCard toModel(TcgDeckCardEntity entity);
}
