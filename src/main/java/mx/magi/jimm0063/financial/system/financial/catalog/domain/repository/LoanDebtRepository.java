package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.model.LoanDebt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.model.LoanDebtId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "loanDebt", path = "loanDebt")
public interface LoanDebtRepository extends JpaRepository<LoanDebt, LoanDebtId> {
}
