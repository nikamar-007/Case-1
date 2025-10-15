package ru.itevents.desktop.util;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;

public final class UIUtils {
    private static final Font DEFAULT_FONT = new Font("Comic Sans MS", Font.PLAIN, 14);

    private UIUtils() {
    }

    public static void configureUiDefaults() {
        UIManager.put("defaultFont", DEFAULT_FONT);
    }

    public static void applyHeaderStyle(JComponent component) {
        component.setBackground(new Color(153, 255, 255));
        component.setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    public static JButton createAccentButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(DEFAULT_FONT.deriveFont(Font.BOLD));
        button.setBackground(new Color(0, 0, 204));
        button.setForeground(Color.WHITE);
        if (listener != null) {
            button.addActionListener(listener);
        }
        return button;
    }

    public static void setBackgroundRecursively(Component component, Color color) {
        component.setBackground(color);
        if (component instanceof JComponent jComponent) {
            for (Component child : jComponent.getComponents()) {
                setBackgroundRecursively(child, color);
            }
        }
    }
}
