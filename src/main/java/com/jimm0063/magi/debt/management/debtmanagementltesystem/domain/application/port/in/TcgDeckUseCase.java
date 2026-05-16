package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeck;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.CreateDeckReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateDeckReq;

import java.util.List;

public interface TcgDeckUseCase {
    List<TcgDeck> findAll();
    TcgDeck findById(Long id);
    TcgDeck create(CreateDeckReq req);
    TcgDeck update(Long id, UpdateDeckReq req);
    void delete(Long id);
}
