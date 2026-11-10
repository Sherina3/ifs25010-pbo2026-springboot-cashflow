package org.delcom.ifs25010pbo2026springbootcashflow.domain.repository;

import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.Transaction;

import java.math.BigDecimal;

/**
 * Port spesifik Transaction yang memperluas generic {@link IRepository}.
 * Menambahkan penyimpanan pemasukan/pengeluaran dengan penomoran ID otomatis.
 */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    /**
     * Menyimpan transaksi pemasukan baru.
     *
     * @return transaksi yang tersimpan (lengkap dengan ID)
     */
    Transaction saveIncome(String description, BigDecimal amount);

    /**
     * Menyimpan transaksi pengeluaran baru.
     *
     * @return transaksi yang tersimpan (lengkap dengan ID)
     */
    Transaction saveExpense(String description, BigDecimal amount);
}
