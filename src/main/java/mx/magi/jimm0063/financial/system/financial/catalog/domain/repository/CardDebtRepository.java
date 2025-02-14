package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.CardDebt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.CardDebtId;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "cardDebt", path = "cardDebt")
public interface CardDebtRepository extends JpaRepository<CardDebt, CardDebtId> {
    List<CardDebt> findAllByDebt(Debt debt);
}
