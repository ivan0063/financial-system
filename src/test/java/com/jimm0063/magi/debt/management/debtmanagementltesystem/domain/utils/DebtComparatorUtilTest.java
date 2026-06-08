package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.utils;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DebtComparatorUtilTest {

    private Debt debt(String hash, int current, int max) {
        Debt d = new Debt();
        d.setHashSum(hash);
        d.setCurrentInstallment(current);
        d.setMaxFinancingTerm(max);
        return d;
    }

    // ── compareDebts ───────────────────────────────────────────────────────────

    @Test
    void compareDebts_returns_true_for_same_hash() {
        Debt a = debt("abc", 1, 3);
        Debt b = debt("abc", 2, 3);
        assertThat(DebtComparatorUtil.compareDebts(a, b)).isTrue();
    }

    @Test
    void compareDebts_returns_false_for_different_hash() {
        assertThat(DebtComparatorUtil.compareDebts(debt("abc", 1, 3), debt("xyz", 1, 3))).isFalse();
    }

    @Test
    void compareDebts_returns_false_when_either_hash_is_null() {
        Debt noHash = new Debt();
        noHash.setCurrentInstallment(1);
        noHash.setMaxFinancingTerm(3);
        assertThat(DebtComparatorUtil.compareDebts(noHash, debt("abc", 1, 3))).isFalse();
        assertThat(DebtComparatorUtil.compareDebts(debt("abc", 1, 3), noHash)).isFalse();
    }

    // ── filterAccountStatementDebts — completed debts ─────────────────────────

    @Test
    void filter_excludes_debts_at_max_installment() {
        Debt completed = debt("h1", 3, 3);  // current == max → completed
        Debt active = debt("h2", 2, 3);

        List<Debt> result = DebtComparatorUtil.filterAccountStatementDebts(List.of(), List.of(completed, active));

        assertThat(result).hasSize(1).extracting(Debt::getHashSum).containsExactly("h2");
    }

    // ── filterAccountStatementDebts — unchanged debts ─────────────────────────

    @Test
    void filter_excludes_debt_that_matches_db_with_same_installment() {
        Debt dbDebt = debt("h1", 2, 6);
        Debt statementDebt = debt("h1", 2, 6);  // same hash, same installment → no change

        List<Debt> result = DebtComparatorUtil.filterAccountStatementDebts(List.of(dbDebt), List.of(statementDebt));

        assertThat(result).isEmpty();
    }

    // ── filterAccountStatementDebts — updated debts ───────────────────────────

    @Test
    void filter_keeps_debt_whose_installment_advanced() {
        Debt dbDebt = debt("h1", 1, 6);      // DB: installment 1
        Debt statementDebt = debt("h1", 2, 6); // statement: installment 2 → update

        List<Debt> result = DebtComparatorUtil.filterAccountStatementDebts(List.of(dbDebt), List.of(statementDebt));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrentInstallment()).isEqualTo(2);
    }

    // ── filterAccountStatementDebts — new debts ───────────────────────────────

    @Test
    void filter_keeps_new_debt_not_present_in_db() {
        Debt dbDebt = debt("h1", 2, 6);
        Debt newDebt = debt("h2", 1, 3);   // different hash → not in DB

        List<Debt> result = DebtComparatorUtil.filterAccountStatementDebts(List.of(dbDebt), List.of(newDebt));

        assertThat(result).hasSize(1).extracting(Debt::getHashSum).containsExactly("h2");
    }

    // ── filterAccountStatementDebts — empty cases ─────────────────────────────

    @Test
    void filter_with_empty_db_returns_all_non_completed_debts() {
        List<Debt> statement = List.of(
                debt("h1", 1, 3),  // active
                debt("h2", 2, 6),  // active
                debt("h3", 3, 3)   // completed — excluded
        );

        List<Debt> result = DebtComparatorUtil.filterAccountStatementDebts(List.of(), statement);

        assertThat(result).hasSize(2).extracting(Debt::getHashSum).containsExactlyInAnyOrder("h1", "h2");
    }

    @Test
    void filter_with_empty_statement_returns_empty() {
        Debt dbDebt = debt("h1", 1, 6);

        List<Debt> result = DebtComparatorUtil.filterAccountStatementDebts(List.of(dbDebt), List.of());

        assertThat(result).isEmpty();
    }

    // ── mixed scenario ────────────────────────────────────────────────────────

    @Test
    void filter_handles_mixed_statement_correctly() {
        Debt dbDebt1 = debt("h1", 1, 6);  // in DB at installment 1
        Debt dbDebt2 = debt("h2", 3, 6);  // in DB at installment 3

        List<Debt> statement = List.of(
                debt("h1", 2, 6),  // h1 advanced to 2 → update → keep
                debt("h2", 3, 6),  // h2 unchanged at 3 → exclude
                debt("h3", 1, 4),  // h3 not in DB → new → keep
                debt("h4", 4, 4)   // h4 completed → exclude
        );

        List<Debt> result = DebtComparatorUtil.filterAccountStatementDebts(
                List.of(dbDebt1, dbDebt2), statement);

        assertThat(result).hasSize(2).extracting(Debt::getHashSum).containsExactlyInAnyOrder("h1", "h3");
    }
}
