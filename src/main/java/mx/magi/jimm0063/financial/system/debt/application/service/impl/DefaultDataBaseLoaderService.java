package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import mx.magi.jimm0063.financial.system.debt.application.component.AccountStatementFactory;
import mx.magi.jimm0063.financial.system.debt.application.component.DebtHashComponent;
import mx.magi.jimm0063.financial.system.debt.application.service.DataBaseLoaderService;
import mx.magi.jimm0063.financial.system.debt.domain.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.domain.enums.PdfExtractorTypes;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.*;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.enums.EntityType;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.repository.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultDataBaseLoaderService implements DataBaseLoaderService {
    private final AccountStatementFactory accountStatementFactory;
    private final CardRepository cardRepository;
    private final DebtRepository debtRepository;
    private final CardDebtRepository cardDebtRepository;
    private final DebtHashComponent debtHashComponent;
    private final PersonLoanRepository personLoanRepository;
    private final PersonLoanDebtRepository personLoanDebtRepository;
    private final ObjectMapper objectMapper;

    public DefaultDataBaseLoaderService(AccountStatementFactory accountStatementFactory, CardRepository cardRepository, DebtRepository debtRepository, CardDebtRepository cardDebtRepository, DebtHashComponent debtHashComponent, PersonLoanRepository personLoanRepository, PersonLoanDebtRepository personLoanDebtRepository, ObjectMapper objectMapper) {
        this.accountStatementFactory = accountStatementFactory;
        this.cardRepository = cardRepository;
        this.debtRepository = debtRepository;
        this.cardDebtRepository = cardDebtRepository;
        this.debtHashComponent = debtHashComponent;
        this.personLoanRepository = personLoanRepository;
        this.personLoanDebtRepository = personLoanDebtRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<DebtModel> loadDebtFromAccountStatement(byte[] accountStatement, String cardCode) throws IOException {
        Card card = this.findCardByCode(cardCode);
        PdfExtractorTypes pdfType = card.getFileType();

        if(PdfExtractorTypes.MANUAL.equals(pdfType)) return new ArrayList<>();

        List<DebtModel> accountStatementDebts = this.accountStatementFactory.getStrategy(pdfType)
                .extractDebt(accountStatement)
                .stream()
                .filter(debt -> debt.getMonthsFinanced() > debt.getMonthsPaid())
                .toList();

        return this.saveDebts(accountStatementDebts, card);
    }

    @Override
    public List<DebtModel> loadDebts(List<DebtModel> debtModels, String cardCode) {
        debtModels = debtModels.stream()
                .filter(debt -> debt.getMonthsFinanced() > debt.getMonthsPaid())
                .toList();
        Card card = this.findCardByCode(cardCode);
        return this.saveDebts(debtModels, card);
    }

    @Transactional
    @Override
    public List<DebtModel> importDebtsFromCSV(MultipartFile file) {
        List<DebtModel> debts = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            boolean isFirstRow = true; // Skip header row

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                // Read values from the Excel sheet
                String name = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                int monthsFinanced = (int) row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue();
                int monthsPaid = (int) row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue();
                double monthAmount = row.getCell(3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue();
                String type = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                String entityId = row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                if(name.isEmpty()) break;

                // Calculate values
                double initialDebtAmount = monthAmount * monthsFinanced;
                double debtPaid = monthAmount * monthsPaid;

                // Create Debt object
                Debt debt = new Debt();
                debt.setDebtId(debtHashComponent.hashId(monthAmount, monthsPaid, monthsFinanced));
                debt.setName(name);
                debt.setMonthsFinanced(monthsFinanced);
                debt.setMonthsPaid(monthsPaid);
                debt.setMonthAmount(monthAmount);
                debt.setInitialDebtAmount(initialDebtAmount);
                debt.setDebtPaid(debtPaid);
                debt.setDisabled(false);

                // Calculate Debt Types
                EntityType entityType = EntityType.valueOf(type);
                debt = switch (entityType) {
                    case CARD -> this.saveCardDebt(debt, entityId);
                    case PERSONAL_LOAN -> this.savePersonLoadDebt(debt, entityId);
                };

                if(Objects.nonNull(debt)) {
                    debts.add(DebtModel.builder()
                            .initialDebtAmount(debt.getInitialDebtAmount())
                            .monthAmount(debt.getMonthAmount())
                            .monthsFinanced(debt.getMonthsFinanced())
                            .monthsPaid(debt.getMonthsPaid())
                            .name(debt.getName())
                            .debtPaid(debt.getDebtPaid())
                            .debtId(debt.getDebtId())
                            .build());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return debts;
    }

    private Debt savePersonLoadDebt(Debt debt, String personLoanCode) {
        PersonLoan personLoan = this.personLoanRepository.findById(personLoanCode)
                .orElseThrow(() -> new RuntimeException("Person loan with code " + personLoanCode + " not found"));
        // Look if the debt already exist
        Optional<?> personLoanOptional = debtRepository.findById(debt.getDebtId());
        if(personLoanOptional.isPresent()) return null;

        debt = this.debtRepository.save(debt);

        PersonLoanDebtId personLoanDebtId = new PersonLoanDebtId();
        personLoanDebtId.setDebtId(debt.getDebtId());
        personLoanDebtId.setLoanCode(personLoanCode);

        PersonLoanDebt personLoanDebt = new PersonLoanDebt();
        personLoanDebt.setId(personLoanDebtId);
        personLoanDebt.setDebt(debt);
        personLoanDebt.setPersonLoanCode(personLoan);
        this.personLoanDebtRepository.save(personLoanDebt);

        return debt;
    }

    private Debt saveCardDebt(Debt debt, String cardCode) {
        Card card = this.findCardByCode(cardCode);
        // Look if the debt already exist
        Optional<?> cardDebtOptional = debtRepository.findById(debt.getDebtId());

        if(cardDebtOptional.isPresent()) return null;

        debt = this.debtRepository.save(debt);

        CardDebtId cardDebtId = new CardDebtId();
        cardDebtId.setDebtId(debt.getDebtId());
        cardDebtId.setCardCode(card.getCardCode());

        CardDebt cardDebt = new CardDebt();
        cardDebt.setId(cardDebtId);
        cardDebt.setDebt(debt);
        cardDebt.setCardCode(card);
        this.cardDebtRepository.save(cardDebt);

        return debt;
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
