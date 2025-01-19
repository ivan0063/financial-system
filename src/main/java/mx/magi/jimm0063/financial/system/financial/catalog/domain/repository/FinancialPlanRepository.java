package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.model.FinancialPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "financialPlan", path = "financialPlan")
public interface FinancialPlanRepository extends JpaRepository<FinancialPlan, String> {
}
