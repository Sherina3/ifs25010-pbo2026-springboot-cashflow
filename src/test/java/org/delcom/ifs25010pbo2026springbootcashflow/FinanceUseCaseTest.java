package org.delcom.ifs25010pbo2026springbootcashflow;

import org.delcom.ifs25010pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25010pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Unit test use case tanpa Spring maupun Vaadin. */
class FinanceUseCaseTest {
    private FinanceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FinanceUseCase(new TransactionRepository());
    }

    @Test
    void balanceIsIncomeMinusExpense() throws ValidationException {
        useCase.addIncome("Gaji", "5000000");
        useCase.addExpense("Makan", "25000");
        assertEquals(new BigDecimal("4975000.00"), useCase.getBalance());
        assertEquals(new BigDecimal("5000000.00"), useCase.getTotalIncome());
        assertEquals(new BigDecimal("25000.00"), useCase.getTotalExpense());
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("  ", "1000"));
        assertThrows(ValidationException.class, () -> useCase.addIncome("Gaji", "-1"));
        assertThrows(ValidationException.class, () -> useCase.addExpense("Makan", "0"));
        assertThrows(ValidationException.class, () -> useCase.addExpense("Makan", "abc"));
        assertThrows(ValidationException.class, () -> useCase.addExpense("Makan", ""));
        assertTrue(useCase.getAllTransactions().isEmpty());
    }

    @Test
    void removeValidatesAndReportsMissingId() throws Exception {
        Transaction created = useCase.addIncome("Gaji", "1000");
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(0));
        assertThrows(EntityNotFoundException.class, () -> useCase.removeTransaction(99));
        useCase.removeTransaction(created.getId());
        assertEquals(BigDecimal.ZERO, useCase.getBalance());
    }

    @Test
    void searchIsCaseInsensitiveAndDoesNotChangeData() throws ValidationException {
        useCase.addIncome("Gaji Bulanan", "1000");
        useCase.addExpense("Makan Siang", "10");
        assertEquals(1, useCase.searchTransactions("gaji").size());
        assertEquals(2, useCase.searchTransactions("").size());
        assertEquals(2, useCase.getAllTransactions().size());
    }

    @Test
    void sortUsesDomainComparators() throws ValidationException {
        useCase.addIncome("A", "300");
        useCase.addExpense("B", "100");
        useCase.addIncome("C", "200");

        assertEquals("B", useCase.sortTransactions(SortOption.AMOUNT_ASC).get(0).getDescription());
        assertEquals("A", useCase.sortTransactions(SortOption.AMOUNT_DESC).get(0).getDescription());
        assertTrue(useCase.sortTransactions(SortOption.INCOME_FIRST).get(0).isIncome());
        assertEquals("B", useCase.sortTransactions(SortOption.EXPENSE_FIRST).get(0).getDescription());
        assertThrows(ValidationException.class, () -> useCase.sortTransactions(null));

        List<Transaction> combined = useCase.findTransactions("c", SortOption.AMOUNT_ASC);
        assertEquals(1, combined.size());
    }
}
