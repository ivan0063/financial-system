package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import lombok.Data;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.enums.EntityType;

@Data
public class AssociatedEntity {
    private String entityId;
    private EntityType entityType;
}
