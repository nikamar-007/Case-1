package ru.itevents.desktop.ui;

import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.service.DirectoryService;
import ru.itevents.desktop.service.PersonService;
import ru.itevents.desktop.ui.dialog.PersonDialog;
import ru.itevents.desktop.ui.model.PersonTableModel;
import ru.itevents.desktop.util.ColorPalette;
import ru.itevents.desktop.util.UIUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.util.List;

public class PeoplePanel extends JPanel {
    private final PersonService personService = new PersonService();
    private final DirectoryService directoryService = new DirectoryService();

    public PeoplePanel() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.PRIMARY_BACKGROUND);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Участники", new PeopleTab(PersonRole.PARTICIPANT));
        tabs.addTab("Модераторы", new PeopleTab(PersonRole.MODERATOR));
        tabs.addTab("Жюри", new PeopleTab(PersonRole.JURY));
        tabs.addTab("Организаторы", new PeopleTab(PersonRole.ORGANIZER));
        add(tabs, BorderLayout.CENTER);
    }

    private class PeopleTab extends JPanel {
        private final PersonRole role;
        private final PersonTableModel tableModel = new PersonTableModel();
        private final JTable table = new JTable(tableModel);

        PeopleTab(PersonRole role) {
            this.role = role;
            setLayout(new BorderLayout());
            setBackground(ColorPalette.PRIMARY_BACKGROUND);
            add(buildToolbar(), BorderLayout.NORTH);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            add(new JScrollPane(table), BorderLayout.CENTER);
            reload();
        }

        private JPanel buildToolbar() {
            JPanel panel = new JPanel();
            panel.setBackground(ColorPalette.PRIMARY_BACKGROUND);
            panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            JButton add = UIUtils.createAccentButton("Добавить", e -> onAdd());
            JButton edit = new JButton("Редактировать");
            edit.addActionListener(e -> onEdit());
            JButton delete = new JButton("Удалить");
            delete.addActionListener(e -> onDelete());
            panel.add(add);
            panel.add(edit);
            panel.add(delete);
            return panel;
        }

        private void reload() {
            List<Person> people = personService.getPeople(role);
            tableModel.setPeople(people);
        }

        private void onAdd() {
            Person person = new Person();
            person.setRole(role);
            PersonDialog dialog = new PersonDialog(person, directoryService);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                personService.save(person);
                reload();
            }
        }

        private void onEdit() {
            Person person = getSelectedPerson();
            if (person == null) {
                return;
            }
            PersonDialog dialog = new PersonDialog(person, directoryService);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                personService.save(person);
                reload();
            }
        }

        private void onDelete() {
            Person person = getSelectedPerson();
            if (person == null) {
                return;
            }
            int choice = JOptionPane.showConfirmDialog(this, "Удалить профиль?", "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                personService.delete(person.getId());
                reload();
            }
        }

        private Person getSelectedPerson() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Выберите запись", "Внимание", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            return tableModel.getPerson(selectedRow);
        }
    }
}
