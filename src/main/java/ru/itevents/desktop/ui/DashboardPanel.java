package ru.itevents.desktop.ui;

import ru.itevents.desktop.service.DashboardService;
import ru.itevents.desktop.util.ColorPalette;
import ru.itevents.desktop.util.UIUtils;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class DashboardPanel extends JPanel {
    private final DashboardService dashboardService = new DashboardService();
    private final JLabel eventsLabel = createMetricLabel();
    private final JLabel activitiesLabel = createMetricLabel();
    private final JLabel participantsLabel = createMetricLabel();
    private final JLabel moderatorsLabel = createMetricLabel();

    public DashboardPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorPalette.PRIMARY_BACKGROUND);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(32, 32, 32, 32));

        add(createMetricPanel("Мероприятия", eventsLabel));
        add(Box.createRigidArea(new Dimension(0, 16)));
        add(createMetricPanel("Активности", activitiesLabel));
        add(Box.createRigidArea(new Dimension(0, 16)));
        add(createMetricPanel("Участники", participantsLabel));
        add(Box.createRigidArea(new Dimension(0, 16)));
        add(createMetricPanel("Модераторы", moderatorsLabel));
        add(Box.createVerticalStrut(32));

        JButton refresh = UIUtils.createAccentButton("Обновить статистику", e -> refreshData());
        refresh.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(refresh);
        refreshData();
    }

    private JPanel createMetricPanel(String title, JLabel valueLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorPalette.SECONDARY_BACKGROUND);
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel header = new JLabel(title);
        header.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(header);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(valueLabel);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JLabel createMetricLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 36));
        return label;
    }

    private void refreshData() {
        eventsLabel.setText(String.valueOf(dashboardService.countEvents()));
        activitiesLabel.setText(String.valueOf(dashboardService.countActivities()));
        participantsLabel.setText(String.valueOf(dashboardService.countParticipants()));
        moderatorsLabel.setText(String.valueOf(dashboardService.countModerators()));
    }
}
