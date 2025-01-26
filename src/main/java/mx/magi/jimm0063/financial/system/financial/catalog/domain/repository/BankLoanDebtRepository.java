package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.BankLoan;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.BankLoanFixedExpnseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "bankLoanDebt", path = "bankLoanDebt")
public interface BankLoanDebtRepository extends JpaRepository<BankLoan, BankLoanFixedExpnseId> {
}
