package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.PersonLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "personLoan", path = "personLoan")
public interface PersonLoanRepository extends JpaRepository<PersonLoan, String> {
    List<PersonLoan> findAllByDisabledFalse();
}
