package mx.magi.jimm0063.financial.system.debt.application.component;

import mx.magi.jimm0063.financial.system.debt.application.enums.PdfExtractorTypes;
import mx.magi.jimm0063.financial.system.debt.application.service.AccountStatementService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccountStatementFactory {
    private final Map<String, AccountStatementService> strategies;

    public AccountStatementFactory(Map<String, AccountStatementService> strategies) {
        this.strategies = strategies;
    }

    public AccountStatementService getStrategy(PdfExtractorTypes strategyName) {
        AccountStatementService strategy = strategies.get(strategyName.toString());
        if (strategy == null) {
            throw new IllegalArgumentException("No such strategy: " + strategyName);
        }
        return strategy;
    }
}
