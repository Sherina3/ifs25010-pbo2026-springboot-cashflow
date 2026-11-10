package org.delcom.ifs25010pbo2026springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Memuat berkas {@code .env} (format KEY=VALUE) ke system properties sebelum Spring Boot start,
 * sehingga placeholder seperti {@code ${APP_PORT:8080}} di application.yaml dapat terisi.
 * Nilai yang sudah diset lewat environment variable atau -D tidak ditimpa.
 *
 * @author Sherina Pasaribu (ifs25010)
 */
public final class DotEnv {

    private DotEnv() {
    }

    /** Memuat .env dari direktori kerja saat ini; diabaikan jika berkas tidak ada. */
    public static void load() {
        load(Path.of(".env"));
    }

    /** Memuat berkas env dari path tertentu. */
    public static void load(Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException ex) {
            return;
        }
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator <= 0) {
                continue;
            }
            String key = line.substring(0, separator).trim();
            String value = stripQuotes(line.substring(separator + 1).trim());
            if (System.getProperty(key) == null && System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        }
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' || first == '\'') && first == last) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
