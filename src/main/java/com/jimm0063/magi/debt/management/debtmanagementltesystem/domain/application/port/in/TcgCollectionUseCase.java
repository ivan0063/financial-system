package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollection;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.CreateCollectionReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateCollectionReq;

import java.util.List;

public interface TcgCollectionUseCase {
    List<TcgCollection> findAll();
    TcgCollection findById(Long id);
    TcgCollection create(CreateCollectionReq req);
    TcgCollection update(Long id, UpdateCollectionReq req);
    void delete(Long id);
}
