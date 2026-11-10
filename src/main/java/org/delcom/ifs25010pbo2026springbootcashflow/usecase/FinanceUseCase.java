package org.delcom.ifs25010pbo2026springbootcashflow.usecase;

import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Use case yang menangani logika bisnis pencatatan keuangan.
 * Tidak melakukan I/O dan tidak mengenal komponen UI; hanya memproses data
 * dan melempar checked exception domain jika operasi tidak valid.
 */
public class FinanceUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;

    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Mengambil semua transaksi. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /** Menambahkan pemasukan baru setelah validasi keterangan dan jumlah. */
    public Transaction addIncome(String description, String amountText) throws ValidationException {
        String validDescription = validateDescription(description);
        BigDecimal amount = parseAmount(amountText);
        return transactionRepository.saveIncome(validDescription, amount);
    }

    /** Menambahkan pengeluaran baru setelah validasi keterangan dan jumlah. */
    public Transaction addExpense(String description, String amountText) throws ValidationException {
        String validDescription = validateDescription(description);
        BigDecimal amount = parseAmount(amountText);
        return transactionRepository.saveExpense(validDescription, amount);
    }

    /** Menghapus transaksi berdasarkan ID; melempar exception jika ID tidak valid / tidak ditemukan. */
    public void removeTransaction(int id) throws EntityNotFoundException, ValidationException {
        if (id <= 0) {
            throw new ValidationException("ID tidak valid");
        }
        boolean deleted = transactionRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return transactionRepository.findAll();
        }
        String lowerKeyword = keyword.trim().toLowerCase();
        return transactionRepository.findBy(
                transaction -> transaction.getDescription().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan semua transaksi sesuai kriteria {@link SortOption}. */
    public List<Transaction> sortTransactions(SortOption option) throws ValidationException {
        if (option == null) {
            throw new ValidationException("Opsi pengurutan tidak valid");
        }
        return transactionRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    /**
     * Mencari berdasarkan kata kunci lalu mengurutkan hasilnya.
     * Kata kunci kosong berarti semua transaksi; {@code option == null} berarti tanpa pengurutan.
     */
    public List<Transaction> findTransactions(String keyword, SortOption option) {
        List<Transaction> found = searchTransactions(keyword);
        if (option == null) {
            return found;
        }
        return found.stream().sorted(option.comparator()).toList();
    }

    /** Total seluruh pemasukan. */
    public BigDecimal getTotalIncome() {
        return transactionRepository.findAll().stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Total seluruh pengeluaran. */
    public BigDecimal getTotalExpense() {
        return transactionRepository.findAll().stream()
                .filter(transaction -> !transaction.isIncome())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Saldo saat ini = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        return transactionRepository.findAll().stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String validateDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan transaksi tidak boleh kosong");
        }
        return description.trim();
    }

    private BigDecimal parseAmount(String amountText) throws ValidationException {
        if (amountText == null || amountText.trim().isEmpty()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException("Jumlah harus berupa angka", ex);
        }
        if (amount.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari nol");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
