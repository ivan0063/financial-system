package mx.magi.jimm0063.financial.system.debt.application.component;

import mx.magi.jimm0063.financial.system.debt.application.enums.PdfExtractorTypes;
import mx.magi.jimm0063.financial.system.debt.application.service.AccountStatement;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AccountStatementFactory {
    private final Map<String, AccountStatement> strategies;

    public AccountStatementFactory(Map<String, AccountStatement> strategies) {
        this.strategies = strategies;
    }

    public AccountStatement getStrategy(PdfExtractorTypes strategyName) {
        AccountStatement strategy = strategies.get(strategyName.toString());
        if (strategy == null) {
            throw new IllegalArgumentException("No such strategy: " + strategyName);
        }
        return strategy;
    }
}
