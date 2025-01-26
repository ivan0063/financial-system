package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import mx.magi.jimm0063.financial.system.debt.application.component.AccountStatementFactory;
import mx.magi.jimm0063.financial.system.debt.application.component.DebtHashComponent;
import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.application.enums.PdfExtractorTypes;
import mx.magi.jimm0063.financial.system.debt.application.service.DataBaseLoaderService;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Card;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.CardDebt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.CardDebtId;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.CardDebtRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.CardRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.DebtRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultDataBaseLoaderService implements DataBaseLoaderService {
    private final AccountStatementFactory accountStatementFactory;
    private final CardRepository cardRepository;
    private final DebtRepository debtRepository;
    private final CardDebtRepository cardDebtRepository;
    private final DebtHashComponent debtHashComponent;

    public DefaultDataBaseLoaderService(AccountStatementFactory accountStatementFactory, CardRepository cardRepository, DebtRepository debtRepository, CardDebtRepository cardDebtRepository, DebtHashComponent debtHashComponent) {
        this.accountStatementFactory = accountStatementFactory;
        this.cardRepository = cardRepository;
        this.debtRepository = debtRepository;
        this.cardDebtRepository = cardDebtRepository;
        this.debtHashComponent = debtHashComponent;
    }

    @Override
    public List<DebtModel> loadDebtFromAccountStatement(byte[] accountStatement, String cardCode) throws IOException {
        Card card = this.findCardByCode(cardCode);
        PdfExtractorTypes pdfType = card.getFileType();

        List<DebtModel> accountStatementDebts = this.accountStatementFactory.getStrategy(pdfType)
                .extractDebt(accountStatement);

        return this.saveDebts(accountStatementDebts, card);
    }

    @Override
    public List<DebtModel> loadDebts(List<DebtModel> debtModels, String cardCode) {
        Card card = this.findCardByCode(cardCode);
        return this.saveDebts(debtModels, card);
    }

    public Card findCardByCode(String cardCode) {
        return cardRepository.findById(cardCode)
                .orElseThrow(() -> new RuntimeException("Bank not found in DB " + cardCode));
    }

    public List<DebtModel> saveDebts(List<DebtModel> accountStatementDebts, Card card) {
        Set<String> debtsInCard = new HashSet<>(card.getCardDebts()
                .stream()
                .map(CardDebt::getDebt)
                .map(Debt::getDebtId)
                .toList());

        List<DebtModel> filteredDebts = accountStatementDebts.stream()
                .filter(accountStatementDebt -> {
                    String debtGenId = debtHashComponent.hashId(accountStatementDebt.getMonthAmount(),
                            accountStatementDebt.getMonthsPaid(),
                            accountStatementDebt.getMonthsFinanced());
                    return !debtsInCard.contains(debtGenId);
                })
                .toList();

        List<Debt> debtsToAdd = filteredDebts.stream()
                .map(accountStatementDebt -> {
                    Debt debt = new Debt();

                    debt.setDebtId(debtHashComponent.hashId(accountStatementDebt.getMonthAmount(),
                            accountStatementDebt.getMonthsPaid(),
                            accountStatementDebt.getMonthsFinanced()));
                    debt.setName(accountStatementDebt.getName().trim());
                    debt.setInitialDebtAmount(accountStatementDebt.getInitialDebtAmount());
                    debt.setDebtPaid(accountStatementDebt.getDebtPaid());
                    debt.setMonthsFinanced(accountStatementDebt.getMonthsFinanced());
                    debt.setMonthsPaid(accountStatementDebt.getMonthsPaid());
                    debt.setMonthAmount(accountStatementDebt.getMonthAmount());
                    debt.setDisabled(false);

                    return debt;
                })
                .collect(Collectors.toList());

        debtsToAdd = this.debtRepository.saveAll(debtsToAdd);

        List<CardDebt> cardDebts = debtsToAdd.stream()
                .map(debtAdded -> {
                    CardDebt cardDebt = new CardDebt();

                    CardDebtId cardDebtId = new CardDebtId();
                    cardDebtId.setDebtId(debtAdded.getDebtId());
                    cardDebtId.setCardCode(card.getCardCode());

                    cardDebt.setId(cardDebtId);
                    cardDebt.setCardCode(card);
                    cardDebt.setDebt(debtAdded);

                    return cardDebt;
                })
                .toList();
        this.cardDebtRepository.saveAll(cardDebts);

        return filteredDebts;
    }
}
