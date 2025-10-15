package ru.itevents.desktop.ui.model;

import ru.itevents.desktop.model.Activity;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityTableModel extends AbstractTableModel {
    private final List<Activity> activities = new ArrayList<>();
    private static final String[] COLUMNS = {"Название", "День", "Начало", "Модератор", "Победитель"};
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public void setActivities(List<Activity> data) {
        activities.clear();
        activities.addAll(data);
        fireTableDataChanged();
    }

    public Activity getActivity(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= activities.size()) {
            return null;
        }
        return activities.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return activities.size();
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
        Activity activity = activities.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> activity.getTitle();
            case 1 -> activity.getDayNumber() != null ? activity.getDayNumber() : "";
            case 2 -> activity.getStartTime() != null ? formatter.format(activity.getStartTime()) : "";
            case 3 -> activity.getModerator() != null ? activity.getModerator().getFullName() : "";
            case 4 -> activity.getWinner() != null ? activity.getWinner().getFullName() : "";
            default -> "";
        };
    }
}
