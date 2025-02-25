package mx.magi.jimm0063.financial.system.payments.application.impl;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.*;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.CardPaymentRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.CardRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.DebtPaymentRepository;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.DebtRepository;
import mx.magi.jimm0063.financial.system.payments.application.PaymentService;
import mx.magi.jimm0063.financial.system.payments.domain.DebtPaymentResponse;
import mx.magi.jimm0063.financial.system.payments.domain.PaymentResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DefualtPaymentService implements PaymentService {
    private final CardRepository cardRepository;
    private final CardPaymentRepository cardPaymentRepository;
    private final DebtPaymentRepository debtPaymentRepository;
    private final DebtRepository debtRepository;
    private final ModelMapper modelMapper;

    public DefualtPaymentService(CardRepository cardRepository, CardPaymentRepository cardPaymentRepository,
                                 DebtPaymentRepository debtPaymentRepository, DebtRepository debtRepository,
                                 ModelMapper modelMapper) {
        this.cardRepository = cardRepository;
        this.cardPaymentRepository = cardPaymentRepository;
        this.debtPaymentRepository = debtPaymentRepository;
        this.debtRepository = debtRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaymentResponse doCardPayment(String cardCode) {
        Card card = cardRepository.findById(cardCode).orElseThrow(() -> new IllegalArgumentException(cardCode + " Card not found in DB"));

        // Create Card Payment
        CardPayment cardPayment = new CardPayment();
        cardPayment.setCard(card);
        cardPayment = this.cardPaymentRepository.save(cardPayment);

         // Getting Debts to update
        List<Debt> debts =  card.getCardDebts().stream()
                .map(CardDebt::getDebt)
                .filter(debt -> debt.getDisabled() == false)
                .toList();

        List<DebtPaymentResponse> debtPayments = new ArrayList<>();
        Double monthAmountUpdated = 0.0;
        for (Debt debt : debts) {
            DebtPayment debtPayment = new DebtPayment();
            debtPayment.setPrevDebtPaid(debt.getDebtPaid());
            debtPayment.setPrevMonthsPaid(debt.getMonthsPaid());
            debtPayment.setMonthsPaid(debt.getMonthsPaid() + 1);
            debtPayment.setDebtPaid(debt.getDebtPaid() + debt.getMonthAmount());
            debtPayment.setCardPayment(cardPayment);
            debtPayment = debtPaymentRepository.save(debtPayment);
            debtPayments.add(modelMapper.map(debtPayment, DebtPaymentResponse.class));

            debt.setDebtPaid(debt.getDebtPaid() + debt.getMonthAmount());
            debt.setMonthsPaid(debt.getMonthsPaid() + 1);

            if(Objects.equals(debt.getMonthsFinanced(), debt.getMonthsPaid())) debt.setDisabled(true);
            debt = debtRepository.save(debt);

            if(!debt.getDisabled()) monthAmountUpdated += debt.getMonthAmount();
        }

        cardPayment.setMonthAmountUpdated(monthAmountUpdated);
        cardPayment = this.cardPaymentRepository.save(cardPayment);
        PaymentResponse paymentResponse = modelMapper.map(cardPayment, PaymentResponse.class);
        paymentResponse.setDebtPayments(debtPayments);

        return paymentResponse;
    }

    @Override
    public PaymentResponse latestCardPayment(String cardCode) {
        List<CardPayment> payments = this.cardPaymentRepository.findAllByCard_CardCodeOrderByCreatedAtDesc(cardCode);

        if (payments.isEmpty()) return null;

        CardPayment cardPayment = payments.get(0);
        return modelMapper.map(cardPayment, PaymentResponse.class);
    }
}
