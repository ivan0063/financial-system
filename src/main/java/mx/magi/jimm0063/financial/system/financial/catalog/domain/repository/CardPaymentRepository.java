package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.CardPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "cardPayment", path = "cardPayment")
public interface CardPaymentRepository extends JpaRepository<CardPayment, String> {
    List<CardPayment> findAllByCard_CardCodeOrderByCreatedAtDesc(String cardCode);
}
