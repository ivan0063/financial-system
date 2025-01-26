package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.FinancialStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "financialStatus", path = "financialStatus")
public interface FinancialStatusRepository extends JpaRepository<FinancialStatus, String> {
}
