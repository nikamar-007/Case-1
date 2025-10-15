package ru.itevents.desktop.ui.model;

import ru.itevents.desktop.model.Event;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventTableModel extends AbstractTableModel {
    private final List<Event> events = new ArrayList<>();
    private static final String[] COLUMNS = {"Название", "Дата начала", "Длительность", "Город", "Куратор"};
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void setEvents(List<Event> data) {
        events.clear();
        events.addAll(data);
        fireTableDataChanged();
    }

    public Event getEvent(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= events.size()) {
            return null;
        }
        return events.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return events.size();
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
        Event event = events.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> event.getTitle();
            case 1 -> event.getStartDate() != null ? formatter.format(event.getStartDate()) : "";
            case 2 -> event.getDurationDays() != null ? event.getDurationDays() : "";
            case 3 -> event.getCity() != null ? event.getCity().getName() : "";
            case 4 -> event.getCurator() != null ? event.getCurator().getFullName() : "";
            default -> "";
        };
    }
}
