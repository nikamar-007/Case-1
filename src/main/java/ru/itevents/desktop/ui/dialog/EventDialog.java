package ru.itevents.desktop.ui.dialog;

import ru.itevents.desktop.model.City;
import ru.itevents.desktop.model.Event;
import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.service.DirectoryService;
import ru.itevents.desktop.service.PersonService;
import ru.itevents.desktop.util.ColorPalette;
import ru.itevents.desktop.util.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DateFormatter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventDialog extends JDialog {
    private final JTextField titleField = new JTextField(30);
    private final JFormattedTextField startDateField;
    private final JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
    private final JComboBox<City> cityCombo = new JComboBox<>();
    private final JComboBox<Person> curatorCombo = new JComboBox<>();

    private final DirectoryService directoryService;
    private final PersonService personService;
    private final Event event;
    private boolean confirmed;

    public EventDialog(Event event, DirectoryService directoryService, PersonService personService) {
        super();
        this.event = event;
        this.directoryService = directoryService;
        this.personService = personService;
        setModal(true);
        setTitle(event.getId() == null ? "Новое мероприятие" : "Редактирование мероприятия");
        setLayout(new BorderLayout());
        setBackground(ColorPalette.PRIMARY_BACKGROUND);

        DateFormatter formatter = new DateFormatter(new SimpleDateFormat("dd.MM.yyyy"));
        startDateField = new JFormattedTextField(formatter);
        startDateField.setColumns(10);

        JPanel form = buildForm();
        add(form, BorderLayout.CENTER);
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
        panel.add(new JLabel("Дата начала (dd.MM.yyyy):"), gbc);
        gbc.gridx = 1;
        panel.add(startDateField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Длительность (дни):"), gbc);
        gbc.gridx = 1;
        panel.add(durationSpinner, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Город:"), gbc);
        gbc.gridx = 1;
        cityCombo.setModel(new DefaultComboBoxModel<>(directoryService.getCities().toArray(new City[0])));
        panel.add(cityCombo, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Куратор:"), gbc);
        gbc.gridx = 1;
        List<Person> organizers = personService.getPeople(PersonRole.ORGANIZER);
        curatorCombo.setModel(new DefaultComboBoxModel<>(organizers.toArray(new Person[0])));
        panel.add(curatorCombo, gbc);

        return panel;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));
        panel.setBackground(ColorPalette.PRIMARY_BACKGROUND);
        JButton ok = UIUtils.createAccentButton("Сохранить", e -> onSave());
        JButton cancel = new JButton("Отмена");
        cancel.addActionListener(e -> dispose());
        panel.add(ok);
        panel.add(cancel);
        return panel;
    }

    private void populateValues() {
        titleField.setText(event.getTitle() != null ? event.getTitle() : "");
        if (event.getStartDate() != null) {
            startDateField.setText(event.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        if (event.getDurationDays() != null) {
            durationSpinner.setValue(event.getDurationDays());
        }
        if (event.getCity() != null) {
            cityCombo.setSelectedItem(event.getCity());
        }
        if (event.getCurator() != null) {
            curatorCombo.setSelectedItem(event.getCurator());
        }
    }

    private void onSave() {
        event.setTitle(titleField.getText().trim());
        String dateText = startDateField.getText();
        if (dateText != null && !dateText.isBlank()) {
            try {
                event.setStartDate(LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } catch (DateTimeParseException ex) {
                startDateField.setValue(null);
                startDateField.requestFocus();
                return;
            }
        }
        event.setDurationDays((Integer) durationSpinner.getValue());
        event.setCity((City) cityCombo.getSelectedItem());
        event.setCurator((Person) curatorCombo.getSelectedItem());
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
