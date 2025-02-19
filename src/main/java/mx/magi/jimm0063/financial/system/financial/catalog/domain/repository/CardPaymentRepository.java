package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.CardPayment;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.DebtPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "cardPayment", path = "cardPayment")
public interface CardPaymentRepository extends JpaRepository<CardPayment, String> {
}
