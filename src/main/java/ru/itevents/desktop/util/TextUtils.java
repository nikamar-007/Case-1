package ru.itevents.desktop.util;

public final class TextUtils {
    private TextUtils() {
    }

    public static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
