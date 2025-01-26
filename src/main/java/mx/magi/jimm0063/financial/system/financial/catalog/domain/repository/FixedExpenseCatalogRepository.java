package mx.magi.jimm0063.financial.system.financial.catalog.domain.repository;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.FixedExpenseCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "fixedExpenseCatalog", path = "fixedExpenseCatalog")
public interface FixedExpenseCatalogRepository extends JpaRepository<FixedExpenseCatalog, String> {
}
