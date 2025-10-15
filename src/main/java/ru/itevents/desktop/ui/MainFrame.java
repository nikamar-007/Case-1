package ru.itevents.desktop.ui;

import ru.itevents.desktop.util.BrandAssets;
import ru.itevents.desktop.util.ColorPalette;
import ru.itevents.desktop.util.UIUtils;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

public class MainFrame extends JFrame {
    private final DashboardPanel dashboardPanel = new DashboardPanel();
    private final EventsPanel eventsPanel = new EventsPanel();
    private final PeoplePanel peoplePanel = new PeoplePanel();

    public MainFrame() {
        super("IT-Events: Управление мероприятием");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 720));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(ColorPalette.PRIMARY_BACKGROUND);
        setIconImage(BrandAssets.createWindowIcon());

        add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Дашборд", dashboardPanel);
        tabs.addTab("Мероприятия", eventsPanel);
        tabs.addTab("Участники", peoplePanel);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        UIUtils.applyHeaderStyle(panel);
        panel.setBackground(ColorPalette.SECONDARY_BACKGROUND);

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        logoLabel.setBorder(new EmptyBorder(0, 0, 0, 16));
        logoLabel.setIcon(BrandAssets.createLogoIcon());
        panel.add(logoLabel, BorderLayout.WEST);

        JLabel title = new JLabel("IT-Events Управление мероприятиями", SwingConstants.LEFT);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        panel.add(title, BorderLayout.CENTER);
        return panel;
    }
}
