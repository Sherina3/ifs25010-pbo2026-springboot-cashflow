package org.delcom.ifs25010pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Presenter yang memformat data dari use case menjadi teks siap tampil.
 * Memisahkan logika tampilan dari entity, use case, dan view.
 */
public class FinancePresenter {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Memformat uang dengan pemisah ribuan dan 2 desimal, mis. 1000000 menjadi "Rp 1,000,000.00". */
    public String formatMoney(BigDecimal amount) {
        return String.format(Locale.US, "Rp %,.2f", amount);
    }

    /** Label jenis transaksi. */
    public String formatType(TransactionType type) {
        return type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
    }

    /** Memformat waktu pencatatan transaksi. */
    public String formatCreatedAt(Transaction transaction) {
        return transaction.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    /** Memformat satu transaksi menjadi baris teks: [id] keterangan | Rp ... | [Jenis]. */
    public String format(Transaction transaction) {
        return String.format(
                "[%d] %s | %s | [%s]",
                transaction.getId(),
                transaction.getDescription(),
                formatMoney(transaction.getAmount()),
                formatType(transaction.getType())
        );
    }

    /** Memformat saldo: "Saldo: Rp ...". */
    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatMoney(balance);
    }

    /** Label pilihan pengurutan untuk ditampilkan di UI. */
    public String formatSortOption(SortOption option) {
        return switch (option) {
            case AMOUNT_ASC -> "Jumlah terkecil";
            case AMOUNT_DESC -> "Jumlah terbesar";
            case INCOME_FIRST -> "Pemasukan dulu";
            case EXPENSE_FIRST -> "Pengeluaran dulu";
        };
    }

    /** Memformat daftar transaksi menjadi teks multi-baris; pesan kosong jika tidak ada data. */
    public String formatList(List<Transaction> transactions, String header, String emptyMessage) {
        StringBuilder builder = new StringBuilder(header);
        if (transactions.isEmpty()) {
            return builder.append('\n').append(emptyMessage).toString();
        }
        String body = transactions.stream()
                .map(this::format)
                .collect(Collectors.joining("\n"));
        return builder.append('\n').append(body).toString();
    }

    /** Memformat daftar semua transaksi. */
    public String showTransactions(List<Transaction> transactions) {
        return formatList(transactions, "Daftar Transaksi:", showEmptyData());
    }

    /** Memformat hasil pencarian berdasarkan kata kunci. */
    public String showSearchResults(List<Transaction> transactions, String keyword) {
        return formatList(transactions, "Hasil Pencarian: \"" + keyword + "\"", showSearchEmpty());
    }

    /** Pesan saat belum ada transaksi. */
    public String showEmptyData() {
        return "- Data transaksi belum tersedia!";
    }

    /** Pesan saat pencarian tidak menemukan hasil. */
    public String showSearchEmpty() {
        return "- Transaksi tidak ditemukan!";
    }

    /** Pesan sukses setelah menambah transaksi. */
    public String showAddSuccess(Transaction transaction) {
        String label = transaction.isIncome() ? "pemasukan" : "pengeluaran";
        return "Berhasil menambah " + label + ": " + format(transaction);
    }

    /** Pesan sukses setelah menghapus transaksi. */
    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    /** Pesan gagal saat menghapus transaksi. */
    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    /** Pesan saat pilihan/aksi tidak dikenali. */
    public String showInvalidChoice() {
        return "[!] Pilihan tidak dimengerti.";
    }

    /** Pesan saat ID tidak valid. */
    public String showInvalidId() {
        return "[!] ID tidak valid!";
    }

    /** Pesan saat opsi pengurutan tidak valid. */
    public String showInvalidSortOption() {
        return "[!] Pilihan pengurutan tidak valid!";
    }

    /** Pesan validasi umum dari domain. */
    public String showValidationError(String message) {
        return "[!] " + message;
    }
}
