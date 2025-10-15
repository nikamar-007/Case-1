package ru.itevents.desktop.ui.model;

import ru.itevents.desktop.model.Person;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PersonTableModel extends AbstractTableModel {
    private final List<Person> people = new ArrayList<>();
    private static final String[] COLUMNS = {"ФИО", "Пол", "Почта", "Телефон", "Страна"};
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void setPeople(List<Person> data) {
        people.clear();
        people.addAll(data);
        fireTableDataChanged();
    }

    public Person getPerson(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= people.size()) {
            return null;
        }
        return people.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return people.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Person person = people.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> person.getFullName();
            case 1 -> person.getGender();
            case 2 -> person.getEmail();
            case 3 -> person.getPhone();
            case 4 -> person.getCountry() != null ? person.getCountry().getName() : "";
            default -> "";
        };
    }
}
