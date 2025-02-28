package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.FixedExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "fixedExpense", path = "fixedExpense")
public interface FixedExpenseRepository extends JpaRepository<FixedExpense, String> {
    List<FixedExpense> findAllByFinalizedFalse();
}
