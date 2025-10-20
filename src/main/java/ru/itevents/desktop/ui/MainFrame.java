package ru.itevents.desktop.ui;

import ru.itevents.desktop.util.BrandAssets;
import ru.itevents.desktop.util.ColorPalette;
import ru.itevents.desktop.util.UIUtils;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;

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
        setIconImage(BrandAssets.getWindowIcon());

        add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Дашборд", dashboardPanel);
        tabs.addTab("Мероприятия", eventsPanel);
        tabs.addTab("Участники", peoplePanel);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        return UIUtils.createHeader("IT-Events Управление мероприятиями");
    }
}
