package ru.itevents.desktop.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public final class DateParser {
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("MM.dd.yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ISO_LOCAL_DATE
    );

    private DateParser() {
    }

    public static LocalDate readDate(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return DateUtil.getLocalDateTime(cell.getNumericCellValue()).toLocalDate();
        }
        return parseDate(cell.getStringCellValue());
    }

    public static LocalDate parseDate(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String normalized = text.trim();
        try {
            double numeric = Double.parseDouble(normalized);
            if (!Double.isNaN(numeric)) {
                return DateUtil.getLocalDateTime(numeric).toLocalDate();
            }
        } catch (NumberFormatException ignored) {
            // fall through to string parsing
        }
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }
        return null;
    }

    public static LocalTime readTime(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            LocalDateTime dateTime = DateUtil.getLocalDateTime(cell.getNumericCellValue());
            return dateTime.toLocalTime();
        }
        return parseTime(cell.getStringCellValue());
    }

    public static LocalTime parseTime(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String normalized = text.trim();
        try {
            double numeric = Double.parseDouble(normalized);
            if (!Double.isNaN(numeric)) {
                return DateUtil.getLocalDateTime(numeric).toLocalTime();
            }
        } catch (NumberFormatException ignored) {
            // proceed to string parsing
        }
        try {
            return LocalTime.parse(normalized, DateTimeFormatter.ofPattern("H:mm").withLocale(Locale.US));
        } catch (DateTimeParseException ignored) {
            // fallback to 24 hour pattern with seconds
        }
        try {
            return LocalTime.parse(normalized, DateTimeFormatter.ofPattern("H:mm:ss"));
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
