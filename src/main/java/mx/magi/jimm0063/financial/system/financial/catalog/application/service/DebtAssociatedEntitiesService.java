package mx.magi.jimm0063.financial.system.financial.catalog.application.service;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.model.AssociatedEntity;

import java.util.List;

public interface DebtAssociatedEntitiesService {
    List<AssociatedEntity> getAssociatedEntities();
}
