package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.PersonLoanDebt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.PersonLoanDebtId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "personLoanDebt", path = "personLoanDebt")
public interface PersonLoanDebtRepository extends JpaRepository<PersonLoanDebt, PersonLoanDebtId> {
    List<PersonLoanDebt> findAllByDebt(Debt debt);
}
