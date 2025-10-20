package ru.itevents.desktop.ui.dialog;

import ru.itevents.desktop.model.Activity;
import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.service.PersonService;
import ru.itevents.desktop.util.ColorPalette;
import ru.itevents.desktop.util.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DateFormatter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ActivityDialog extends JDialog {
    private final JTextField titleField = new JTextField(30);
    private final JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private final JFormattedTextField timeField;
    private final JComboBox<Person> moderatorCombo = new JComboBox<>();
    private final JComboBox<Person> winnerCombo = new JComboBox<>();
    private final JList<Person> juryList = new JList<>();

    private final Activity activity;
    private boolean confirmed;

    public ActivityDialog(Activity activity, PersonService personService) {
        this.activity = activity;
        setModal(true);
        setTitle(activity.getId() == null ? "Новая активность" : "Редактирование активности");
        setLayout(new BorderLayout());
        setBackground(ColorPalette.PRIMARY_BACKGROUND);
        add(UIUtils.createHeader(getTitle()), BorderLayout.NORTH);

        DateFormatter formatter = new DateFormatter(new SimpleDateFormat("HH:mm"));
        timeField = new JFormattedTextField(formatter);
        timeField.setColumns(5);

        List<Person> moderators = personService.getPeople(PersonRole.MODERATOR);
        moderatorCombo.setModel(new DefaultComboBoxModel<>(moderators.toArray(new Person[0])));
        List<Person> participants = personService.getPeople(PersonRole.PARTICIPANT);
        winnerCombo.setModel(new DefaultComboBoxModel<>(participants.toArray(new Person[0])));
        List<Person> juryMembers = personService.getPeople(PersonRole.JURY);
        DefaultListModel<Person> model = new DefaultListModel<>();
        juryMembers.forEach(model::addElement);
        juryList.setModel(model);
        juryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        populateValues();
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(ColorPalette.PRIMARY_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Название:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("День:"), gbc);
        gbc.gridx = 1;
        panel.add(daySpinner, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Начало (HH:mm):"), gbc);
        gbc.gridx = 1;
        panel.add(timeField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Модератор:"), gbc);
        gbc.gridx = 1;
        panel.add(moderatorCombo, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Победитель:"), gbc);
        gbc.gridx = 1;
        panel.add(winnerCombo, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Жюри:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        juryList.setVisibleRowCount(6);
        panel.add(new javax.swing.JScrollPane(juryList), gbc);

        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.PRIMARY_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));
        JButton ok = UIUtils.createAccentButton("Сохранить", e -> onSave());
        JButton cancel = new JButton("Отмена");
        cancel.addActionListener(e -> dispose());
        panel.add(ok);
        panel.add(cancel);
        return panel;
    }

    private void populateValues() {
        titleField.setText(activity.getTitle() != null ? activity.getTitle() : "");
        if (activity.getDayNumber() != null) {
            daySpinner.setValue(activity.getDayNumber());
        }
        if (activity.getStartTime() != null) {
            timeField.setText(activity.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        if (activity.getModerator() != null) {
            moderatorCombo.setSelectedItem(activity.getModerator());
        }
        if (activity.getWinner() != null) {
            winnerCombo.setSelectedItem(activity.getWinner());
        }
        if (!activity.getJury().isEmpty()) {
            int[] indices = activity.getJury().stream().mapToInt(person -> findIndexInList(person)).filter(i -> i >= 0).toArray();
            juryList.setSelectedIndices(indices);
        }
    }

    private int findIndexInList(Person person) {
        for (int i = 0; i < juryList.getModel().getSize(); i++) {
            Person element = juryList.getModel().getElementAt(i);
            if (element.getId() != null && element.getId().equals(person.getId())) {
                return i;
            }
            if (element.getFullName().equals(person.getFullName())) {
                return i;
            }
        }
        return -1;
    }

    private void onSave() {
        activity.setTitle(titleField.getText().trim());
        activity.setDayNumber((Integer) daySpinner.getValue());
        String timeText = timeField.getText();
        if (timeText != null && !timeText.isBlank()) {
            try {
                activity.setStartTime(LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm")));
            } catch (DateTimeParseException ex) {
                timeField.setValue(null);
                timeField.requestFocus();
                return;
            }
        }
        activity.setModerator((Person) moderatorCombo.getSelectedItem());
        activity.setWinner((Person) winnerCombo.getSelectedItem());
        activity.getJury().clear();
        activity.getJury().addAll(juryList.getSelectedValuesList());
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
