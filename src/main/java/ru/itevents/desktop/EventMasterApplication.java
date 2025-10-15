package ru.itevents.desktop;

import com.formdev.flatlaf.FlatLightLaf;
import ru.itevents.desktop.ui.MainFrame;
import ru.itevents.desktop.util.UIUtils;

import javax.swing.SwingUtilities;

public final class EventMasterApplication {
    private EventMasterApplication() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            UIUtils.configureUiDefaults();
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
