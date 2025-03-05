package mx.magi.jimm0063.financial.system.financial.catalog.application.service.impl;

import mx.magi.jimm0063.financial.system.financial.catalog.application.service.DebtAssociatedEntitiesService;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.enums.EntityType;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.model.AssociatedEntity;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.CardRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.PersonLoanRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultDebtAssociatedEntitiesService implements DebtAssociatedEntitiesService {
    private final CardRepository cardRepository;
    private final PersonLoanRepository personLoanRepository;

    public DefaultDebtAssociatedEntitiesService(CardRepository cardRepository,
                                                PersonLoanRepository personLoanRepository) {
        this.cardRepository = cardRepository;
        this.personLoanRepository = personLoanRepository;
    }

    @Override
    public List<AssociatedEntity> getAssociatedEntities() {
        List<AssociatedEntity> associatedEntities = new ArrayList<>();

        this.cardRepository.findAllByEnabledTrue().forEach(card -> {
            AssociatedEntity associatedEntity = new AssociatedEntity();
            associatedEntity.setEntityId(card.getCardCode());
            associatedEntity.setEntityType(EntityType.CARD);
            associatedEntities.add(associatedEntity);
        });
        this.personLoanRepository.findAllByDisabledFalse().forEach(personLoan -> {
            AssociatedEntity associatedEntity = new AssociatedEntity();
            associatedEntity.setEntityId(personLoan.getLoanCode());
            associatedEntity.setEntityType(EntityType.PERSONAL_LOAN);
            associatedEntities.add(associatedEntity);
        });

        return associatedEntities;
    }
}
