package ru.itevents.desktop.ui.dialog;

import ru.itevents.desktop.model.Country;
import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.service.DirectoryService;
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
import javax.swing.JTextField;
import javax.swing.text.DateFormatter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PersonDialog extends JDialog {
    private final JTextField nameField = new JTextField(30);
    private final JTextField genderField = new JTextField(10);
    private final JTextField emailField = new JTextField(25);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField specializationField = new JTextField(25);
    private final JTextField focusField = new JTextField(25);
    private final JTextField passwordField = new JTextField(20);
    private final JTextField photoField = new JTextField(25);
    private final JComboBox<Country> countryCombo = new JComboBox<>();
    private final JFormattedTextField birthDateField;

    private final DirectoryService directoryService;
    private final Person person;
    private boolean confirmed;

    public PersonDialog(Person person, DirectoryService directoryService) {
        this.person = person;
        this.directoryService = directoryService;
        setModal(true);
        setTitle(person.getId() == null ? "Новый профиль" : "Редактирование профиля");
        setLayout(new BorderLayout());
        setBackground(ColorPalette.PRIMARY_BACKGROUND);

        DateFormatter formatter = new DateFormatter(new SimpleDateFormat("dd.MM.yyyy"));
        birthDateField = new JFormattedTextField(formatter);
        birthDateField.setColumns(10);

        countryCombo.setModel(new DefaultComboBoxModel<>(directoryService.getCountries().toArray(new Country[0])));

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
        panel.add(new JLabel("ФИО:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Пол:"), gbc);
        gbc.gridx = 1;
        panel.add(genderField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Почта:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Дата рождения:"), gbc);
        gbc.gridx = 1;
        panel.add(birthDateField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Страна:"), gbc);
        gbc.gridx = 1;
        panel.add(countryCombo, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Направление/Специализация:"), gbc);
        gbc.gridx = 1;
        panel.add(specializationField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Фокус/Мероприятие:"), gbc);
        gbc.gridx = 1;
        panel.add(focusField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Пароль/код доступа:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Фото (путь):"), gbc);
        gbc.gridx = 1;
        panel.add(photoField, gbc);

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
        nameField.setText(person.getFullName() != null ? person.getFullName() : "");
        genderField.setText(person.getGender() != null ? person.getGender() : "");
        emailField.setText(person.getEmail() != null ? person.getEmail() : "");
        phoneField.setText(person.getPhone() != null ? person.getPhone() : "");
        if (person.getBirthDate() != null) {
            birthDateField.setText(person.getBirthDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        if (person.getCountry() != null) {
            countryCombo.setSelectedItem(person.getCountry());
        }
        specializationField.setText(person.getSpecialization() != null ? person.getSpecialization() : "");
        focusField.setText(person.getFocus() != null ? person.getFocus() : "");
        passwordField.setText(person.getPasswordHash() != null ? person.getPasswordHash() : "");
        photoField.setText(person.getPhotoPath() != null ? person.getPhotoPath() : "");

        if (person.getRole() == PersonRole.PARTICIPANT) {
            focusField.setEnabled(false);
        }
    }

    private void onSave() {
        person.setFullName(nameField.getText().trim());
        person.setGender(genderField.getText().trim());
        person.setEmail(emailField.getText().trim());
        person.setPhone(phoneField.getText().trim());
        String birthText = birthDateField.getText();
        if (birthText != null && !birthText.isBlank()) {
            try {
                person.setBirthDate(LocalDate.parse(birthText, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } catch (DateTimeParseException ex) {
                birthDateField.setValue(null);
                birthDateField.requestFocus();
                return;
            }
        }
        person.setCountry((Country) countryCombo.getSelectedItem());
        person.setSpecialization(specializationField.getText().trim());
        person.setFocus(focusField.getText().trim());
        person.setPasswordHash(passwordField.getText().trim());
        person.setPhotoPath(photoField.getText().trim());
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
