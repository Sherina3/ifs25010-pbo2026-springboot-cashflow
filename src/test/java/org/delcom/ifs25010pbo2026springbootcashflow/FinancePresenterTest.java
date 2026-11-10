package org.delcom.ifs25010pbo2026springbootcashflow;

import org.delcom.ifs25010pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancePresenterTest {
    private final FinancePresenter presenter = new FinancePresenter();

    @Test
    void formatsMoneyWithThousandsSeparatorAndTwoDecimals() {
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(new BigDecimal("1000000")));
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(new BigDecimal("4975000")));
    }

    @Test
    void formatsTransactionLine() {
        Transaction transaction = new Transaction(3, "Gaji", new BigDecimal("1000000"), TransactionType.INCOME);
        assertEquals("[3] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.format(transaction));
    }
}
