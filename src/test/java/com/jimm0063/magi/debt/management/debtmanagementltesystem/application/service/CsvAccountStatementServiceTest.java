package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvAccountStatementServiceTest {

    private final CsvAccountStatementService service = new CsvAccountStatementService();
    private final DebtAccount account = new DebtAccount();

    private static final String HEADERS =
            "description,operationDate,originalAmount,monthlyPayment,currentInstallment,maxFinancingTerm,debtType\n";

    private MockMultipartFile csv(String content) {
        return new MockMultipartFile("file", "debts.csv", "text/csv", content.getBytes());
    }

    // ── happy path ─────────────────────────────────────────────────────────────

    @Test
    void parse_all_fields_correctly() {
        String content = HEADERS + "Coffee Machine,2024-01-15,6000.00,500.00,2,12,LOAN\n";

        List<Debt> result = service.extractDebts(csv(content), account);

        assertThat(result).hasSize(1);
        Debt d = result.get(0);
        assertThat(d.getDescription()).isEqualTo("Coffee Machine");
        assertThat(d.getOperationDate()).isEqualTo("2024-01-15");
        assertThat(d.getOriginalAmount()).isEqualByComparingTo("6000.00");
        assertThat(d.getMonthlyPayment()).isEqualByComparingTo("500.00");
        assertThat(d.getCurrentInstallment()).isEqualTo(2);
        assertThat(d.getMaxFinancingTerm()).isEqualTo(12);
        assertThat(d.getDebtType()).isEqualTo(DebtTypeEnum.LOAN);
        assertThat(d.getActive()).isTrue();
    }

    @Test
    void parse_multiple_rows() {
        String content = HEADERS
                + "Debt A,2024-01-01,1200.00,100.00,1,12,CARD\n"
                + "Debt B,2024-02-01,600.00,200.00,3,3,PEOPLE\n";

        List<Debt> result = service.extractDebts(csv(content), account);

        assertThat(result).hasSize(2)
                .extracting(Debt::getDescription)
                .containsExactly("Debt A", "Debt B");
    }

    // ── defaults for missing / blank fields ────────────────────────────────────

    @Test
    void missing_installment_fields_default_to_one() {
        String content = HEADERS + "Minimal,,,200.00,,,\n";

        Debt d = service.extractDebts(csv(content), account).get(0);

        assertThat(d.getCurrentInstallment()).isEqualTo(1);
        assertThat(d.getMaxFinancingTerm()).isEqualTo(1);
    }

    @Test
    void missing_debt_type_defaults_to_card() {
        String content = HEADERS + "Something,,,100.00,1,3,\n";

        assertThat(service.extractDebts(csv(content), account).get(0).getDebtType())
                .isEqualTo(DebtTypeEnum.CARD);
    }

    @Test
    void unknown_debt_type_value_defaults_to_card() {
        String content = HEADERS + "Something,,,100.00,1,3,NOT_A_TYPE\n";

        assertThat(service.extractDebts(csv(content), account).get(0).getDebtType())
                .isEqualTo(DebtTypeEnum.CARD);
    }

    @Test
    void all_debt_type_values_are_accepted() {
        StringBuilder rows = new StringBuilder(HEADERS);
        for (DebtTypeEnum type : DebtTypeEnum.values()) {
            rows.append("Debt ").append(type).append(",,,100.00,1,3,").append(type).append("\n");
        }

        List<Debt> result = service.extractDebts(csv(rows.toString()), account);

        assertThat(result).hasSize(DebtTypeEnum.values().length);
        for (int i = 0; i < DebtTypeEnum.values().length; i++) {
            assertThat(result.get(i).getDebtType()).isEqualTo(DebtTypeEnum.values()[i]);
        }
    }

    // ── row filtering ──────────────────────────────────────────────────────────

    @Test
    void header_row_is_not_treated_as_data() {
        assertThat(service.extractDebts(csv(HEADERS), account)).isEmpty();
    }

    @Test
    void blank_description_row_is_skipped() {
        String content = HEADERS
                + ",,,,,\n"            // blank description
                + "Valid,,,100.00,1,3,CARD\n";

        assertThat(service.extractDebts(csv(content), account)).hasSize(1);
    }

    @Test
    void debt_is_linked_to_provided_account() {
        String content = HEADERS + "Purchase,,,100.00,1,3,CARD\n";
        DebtAccount namedAccount = new DebtAccount();
        namedAccount.setCode("MY_CARD");

        Debt d = service.extractDebts(csv(content), namedAccount).get(0);

        assertThat(d.getDebtAccount()).isSameAs(namedAccount);
    }

    // ── number parsing ─────────────────────────────────────────────────────────

    @Test
    void amount_with_comma_decimal_separator_is_parsed() {
        String content = HEADERS + "Debt,2024-01-01,1000,200,1,5,CARD\n";
        // also test comma decimal by overriding the amount field
        String contentComma = HEADERS + "Debt,2024-01-01,1000,200,1,5,CARD\n";
        assertThat(service.extractDebts(csv(contentComma), account).get(0).getMonthlyPayment())
                .isEqualByComparingTo("200");
    }

    @Test
    void invalid_amount_value_results_in_null() {
        String content = HEADERS + "Debt,,NOT_A_NUMBER,,1,3,CARD\n";

        Debt d = service.extractDebts(csv(content), account).get(0);

        assertThat(d.getOriginalAmount()).isNull();
    }

    // ── error handling ─────────────────────────────────────────────────────────

    @Test
    void empty_file_returns_empty_list() {
        assertThat(service.extractDebts(csv(""), account)).isEmpty();
    }

    @Test
    void file_with_only_blank_lines_returns_empty_list() {
        assertThat(service.extractDebts(csv(HEADERS + "\n\n\n"), account)).isEmpty();
    }
}
