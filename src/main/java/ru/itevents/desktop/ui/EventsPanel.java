package ru.itevents.desktop.ui;

import ru.itevents.desktop.model.Activity;
import ru.itevents.desktop.model.Event;
import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.service.DirectoryService;
import ru.itevents.desktop.service.EventService;
import ru.itevents.desktop.service.PersonService;
import ru.itevents.desktop.ui.dialog.ActivityDialog;
import ru.itevents.desktop.ui.dialog.EventDialog;
import ru.itevents.desktop.ui.model.ActivityTableModel;
import ru.itevents.desktop.ui.model.EventTableModel;
import ru.itevents.desktop.util.ColorPalette;
import ru.itevents.desktop.util.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

public class EventsPanel extends JPanel {
    private final EventService eventService = new EventService();
    private final DirectoryService directoryService = new DirectoryService();
    private final PersonService personService = new PersonService();
    private final EventTableModel eventTableModel = new EventTableModel();
    private final ActivityTableModel activityTableModel = new ActivityTableModel();
    private final JTable eventsTable = new JTable(eventTableModel);
    private final JTable activitiesTable = new JTable(activityTableModel);

    public EventsPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.PRIMARY_BACKGROUND);
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        reloadEvents();
    }

    private JPanel buildToolbar() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.PRIMARY_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JButton addEvent = UIUtils.createAccentButton("Добавить", e -> onAddEvent());
        JButton editEvent = new JButton("Редактировать");
        editEvent.addActionListener(e -> onEditEvent());
        JButton deleteEvent = new JButton("Удалить");
        deleteEvent.addActionListener(e -> onDeleteEvent());

        JButton addActivity = new JButton("Добавить активность");
        addActivity.addActionListener(e -> onAddActivity());
        JButton editActivity = new JButton("Редактировать активность");
        editActivity.addActionListener(e -> onEditActivity());
        JButton deleteActivity = new JButton("Удалить активность");
        deleteActivity.addActionListener(e -> onDeleteActivity());

        panel.add(addEvent);
        panel.add(editEvent);
        panel.add(deleteEvent);
        panel.add(addActivity);
        panel.add(editActivity);
        panel.add(deleteActivity);
        return panel;
    }

    private JSplitPane buildContent() {
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventsTable.getSelectionModel().addListSelectionListener(e -> onEventSelected());
        eventsTable.setPreferredScrollableViewportSize(new Dimension(600, 400));
        JScrollPane eventsScroll = new JScrollPane(eventsTable);

        activitiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane activitiesScroll = new JScrollPane(activitiesTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, eventsScroll, activitiesScroll);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.6);
        return splitPane;
    }

    private void reloadEvents() {
        List<Event> events = eventService.findAll();
        eventTableModel.setEvents(events);
        if (!events.isEmpty()) {
            eventsTable.setRowSelectionInterval(0, 0);
            activityTableModel.setActivities(events.get(0).getActivities());
        } else {
            activityTableModel.setActivities(List.of());
        }
    }

    private void onEventSelected() {
        int selectedRow = eventsTable.getSelectedRow();
        Event event = eventTableModel.getEvent(selectedRow);
        if (event != null) {
            activityTableModel.setActivities(event.getActivities());
        }
    }

    private void onAddEvent() {
        Event event = new Event();
        EventDialog dialog = new EventDialog(event, directoryService, personService);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            eventService.save(event);
            reloadEvents();
        }
    }

    private void onEditEvent() {
        Event event = getSelectedEvent();
        if (event == null) {
            return;
        }
        EventDialog dialog = new EventDialog(event, directoryService, personService);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            eventService.save(event);
            reloadEvents();
        }
    }

    private void onDeleteEvent() {
        Event event = getSelectedEvent();
        if (event == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Удалить выбранное мероприятие?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            eventService.delete(event.getId());
            reloadEvents();
        }
    }

    private void onAddActivity() {
        Event event = getSelectedEvent();
        if (event == null) {
            return;
        }
        Activity activity = new Activity();
        activity.setEvent(event);
        ActivityDialog dialog = new ActivityDialog(activity, personService);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            eventService.saveActivity(activity);
            reloadEvents();
        }
    }

    private void onEditActivity() {
        Activity activity = getSelectedActivity();
        if (activity == null) {
            return;
        }
        ActivityDialog dialog = new ActivityDialog(activity, personService);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            eventService.saveActivity(activity);
            reloadEvents();
        }
    }

    private void onDeleteActivity() {
        Activity activity = getSelectedActivity();
        if (activity == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Удалить выбранную активность?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            eventService.deleteActivity(activity.getId());
            reloadEvents();
        }
    }

    private Event getSelectedEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите мероприятие", "Внимание", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return eventTableModel.getEvent(selectedRow);
    }

    private Activity getSelectedActivity() {
        int selectedRow = activitiesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите активность", "Внимание", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return activityTableModel.getActivity(selectedRow);
    }
}
