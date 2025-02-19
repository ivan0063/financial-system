package mx.magi.jimm0063.financial.system.payments.application.impl;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.*;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.CardPaymentRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.CardRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.DebtPaymentRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.DebtRepository;
import mx.magi.jimm0063.financial.system.payments.application.PaymentService;
import mx.magi.jimm0063.financial.system.payments.domain.PaymentResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefualtPaymentService implements PaymentService {
    private final CardRepository cardRepository;
    private final CardPaymentRepository cardPaymentRepository;
    private final DebtPaymentRepository debtPaymentRepository;
    private final DebtRepository debtRepository;

    public DefualtPaymentService(CardRepository cardRepository, CardPaymentRepository cardPaymentRepository, DebtPaymentRepository debtPaymentRepository, DebtRepository debtRepository) {
        this.cardRepository = cardRepository;
        this.cardPaymentRepository = cardPaymentRepository;
        this.debtPaymentRepository = debtPaymentRepository;
        this.debtRepository = debtRepository;
    }

    @Override
    public PaymentResponse doCardPayment(String cardCode) {
        Card card = cardRepository.findById(cardCode).orElseThrow(() -> new IllegalArgumentException(cardCode + " Card not found in DB"));

        // Create Card Payment
        CardPayment cardPayment = new CardPayment();
        cardPayment = this.cardPaymentRepository.save(cardPayment);

         // Getting Debts to update
        List<Debt> debts =  card.getCardDebts().stream()
                .map(CardDebt::getDebt)
                .toList();

        List<DebtPayment> debtPayments = new ArrayList<>();

        for (Debt debt : debts) {
            DebtPayment debtPayment = new DebtPayment();
            debtPayment.setPrevDebtPaid(debt.getDebtPaid());
            debtPayment.setPrevMonthsPaid(debt.getMonthsPaid());
            debtPayment.setMonthsPaid(debt.getMonthsPaid() + 1);
            debtPayment.setDebtPaid(debt.getDebtPaid() + debt.getMonthAmount());
            debtPayment.setCardPayment(cardPayment);
            debtPaymentRepository.save(debtPayment);

            debt.setDebtPaid(debt.getDebtPaid() + debt.getMonthAmount());
            debt.setMonthsPaid(debt.getMonthsPaid() + 1);
            debtRepository.save(debt);
        }

        return null;
    }
}
