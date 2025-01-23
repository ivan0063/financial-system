package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.application.service.AccountStatement;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service("LIVERPOOL")
public class LiverpoolAccountStatementImpl implements AccountStatement {
    @Override
    public List<DebtModel> extractDebt(byte[] pdfFile) throws IOException {
        return List.of();
    }
}
