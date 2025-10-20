package ru.itevents.desktop.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ru.itevents.desktop.model.Activity;
import ru.itevents.desktop.model.Country;
import ru.itevents.desktop.model.Event;
import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.util.DateParser;
import ru.itevents.desktop.util.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DataImportService {
    private final DirectoryService directoryService = new DirectoryService();
    private final PersonService personService = new PersonService();
    private final EventService eventService = new EventService();
    private final Map<Integer, Country> countriesByRowIndex = new HashMap<>();
    private final Map<Integer, Country> countriesByIsoNumeric = new HashMap<>();
    private final Map<String, Country> countriesByAlpha2 = new HashMap<>();
    private final Map<String, Country> countriesByOriginalCode = new HashMap<>();

    public void importCountries(Path path) throws IOException {
        countriesByRowIndex.clear();
        countriesByIsoNumeric.clear();
        countriesByAlpha2.clear();
        countriesByOriginalCode.clear();
        List<List<String>> rows = readTable(path);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row == null) {
                continue;
            }
            String name = readString(row, 0);
            if (name == null) {
                continue;
            }
            Country country = new Country();
            country.setName(name);
            country.setEnglishName(readString(row, 1));
            String isoAlpha2 = readString(row, 2);
            if (isoAlpha2 != null) {
                country.setIsoAlpha2(isoAlpha2.trim().toUpperCase(Locale.ROOT));
            }
            String isoNumericRaw = readString(row, 3);
            Integer isoNumeric = parseInteger(isoNumericRaw);
            country.setIsoNumeric(isoNumeric);
            Country saved = directoryService.saveCountry(country);
            countriesByRowIndex.put(i, saved);
            if (saved.getIsoAlpha2() != null) {
                String alphaKey = saved.getIsoAlpha2().toUpperCase(Locale.ROOT);
                countriesByAlpha2.put(alphaKey, saved);
                countriesByOriginalCode.put(alphaKey, saved);
            }
            if (isoNumericRaw != null && !isoNumericRaw.isBlank()) {
                countriesByOriginalCode.put(isoNumericRaw.trim(), saved);
            }
            if (isoNumeric != null) {
                countriesByIsoNumeric.put(isoNumeric, saved);
                countriesByOriginalCode.put(String.valueOf(isoNumeric), saved);
            }
        }
    }

    public void importCities(Path path, String defaultCountryName) throws IOException {
        Country defaultCountry = resolveCountry(defaultCountryName);
        List<List<String>> rows = readTable(path);
        for (List<String> row : rows) {
            if (row == null) {
                continue;
            }
            String name = null;
            for (String value : row) {
                if (value != null && !value.isBlank() && !value.equalsIgnoreCase("не призн.")) {
                    name = value;
                }
            }
            if (name != null) {
                directoryService.ensureCity(name, defaultCountry);
            }
        }
    }

    public void importPeople(Path path, PersonRole role) throws IOException {
        List<List<String>> rows = readTable(path);
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row == null) {
                continue;
            }
            String fullName = readString(row, 0);
            if (fullName == null) {
                continue;
            }
            Person person = Optional.ofNullable(personService.findByName(fullName)).orElseGet(Person::new);
            person.setRole(role);
            person.setFullName(fullName);
            person.setGender(readString(row, 1));
            person.setEmail(readString(row, 2));
            LocalDate birthDate = DateParser.parseDate(readString(row, 3));
            person.setBirthDate(birthDate);
            String countryCode = readString(row, 4);
            Country country = resolveCountry(countryCode);
            person.setCountry(country);
            person.setPhone(readString(row, 5));
            person.setSpecialization(readString(row, 6));
            if (role == PersonRole.MODERATOR) {
                person.setFocus(readString(row, 7));
                person.setPasswordHash(readString(row, 8));
                person.setPhotoPath(readString(row, 9));
            } else if (role == PersonRole.JURY) {
                person.setFocus(readString(row, 6));
                person.setPasswordHash(readString(row, 7));
                person.setPhotoPath(readString(row, 8));
            } else {
                person.setPasswordHash(readString(row, 6));
                person.setPhotoPath(readString(row, 7));
            }
            personService.save(person);
        }
    }

    public void importEventsAndActivities(Path path) throws IOException {
        List<List<String>> rows = readTable(path);
        Event currentEvent = null;
        Map<String, Event> eventsByName = new HashMap<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row == null) {
                continue;
            }
            String eventTitle = readString(row, 1);
            String activityTitle = readString(row, 4);
            if (eventTitle != null && activityTitle == null) {
                currentEvent = eventsByName.computeIfAbsent(eventTitle, key -> {
                    Event event = Optional.ofNullable(eventService.findAll().stream()
                            .filter(e -> Objects.equals(e.getTitle(), key))
                            .findFirst()
                            .orElse(null))
                            .orElseGet(Event::new);
                    event.setTitle(key);
                    event.setStartDate(DateParser.parseDate(readString(row, 2)));
                    String daysText = readString(row, 3);
                    if (daysText != null) {
                        try {
                            event.setDurationDays(Integer.parseInt(daysText));
                        } catch (NumberFormatException ignored) {
                            // skip invalid numbers
                        }
                    }
                    String curatorName = readString(row, 13);
                    if (curatorName != null) {
                        Person curator = Optional.ofNullable(personService.findByName(curatorName))
                                .orElseGet(() -> {
                                    Person newPerson = new Person();
                                    newPerson.setFullName(curatorName);
                                    newPerson.setRole(PersonRole.ORGANIZER);
                                    return personService.save(newPerson);
                                });
                        event.setCurator(curator);
                    }
                    eventService.save(event);
                    return event;
                });
            }
            if (activityTitle != null && currentEvent != null) {
                Activity activity = new Activity();
                activity.setEvent(currentEvent);
                activity.setTitle(activityTitle);
                String dayNumber = readString(row, 5);
                if (dayNumber != null) {
                    try {
                        activity.setDayNumber(Integer.parseInt(dayNumber));
                    } catch (NumberFormatException ignored) {
                    }
                }
                LocalTime time = DateParser.parseTime(readString(row, 6));
                activity.setStartTime(time);
                String moderatorName = readString(row, 7);
                if (moderatorName != null) {
                    Person moderator = Optional.ofNullable(personService.findByName(moderatorName))
                            .orElseGet(() -> {
                                Person newPerson = new Person();
                                newPerson.setFullName(moderatorName);
                                newPerson.setRole(PersonRole.MODERATOR);
                                return personService.save(newPerson);
                            });
                    activity.setModerator(moderator);
                }
                List<Person> jury = new ArrayList<>();
                for (int col = 8; col <= 12; col++) {
                    String juryName = readString(row, col);
                    if (juryName != null) {
                        Person juror = Optional.ofNullable(personService.findByName(juryName))
                                .orElseGet(() -> {
                                    Person newPerson = new Person();
                                    newPerson.setFullName(juryName);
                                    newPerson.setRole(PersonRole.JURY);
                                    return personService.save(newPerson);
                                });
                        jury.add(juror);
                    }
                }
                activity.getJury().clear();
                activity.getJury().addAll(jury);
                String winnerName = readString(row, 13);
                if (winnerName != null) {
                    Person winner = Optional.ofNullable(personService.findByName(winnerName))
                            .orElseGet(() -> {
                                Person newPerson = new Person();
                                newPerson.setFullName(winnerName);
                                newPerson.setRole(PersonRole.PARTICIPANT);
                                return personService.save(newPerson);
                            });
                    activity.setWinner(winner);
                }
                eventService.saveActivity(activity);
            }
        }
    }

    private List<List<String>> readTable(Path path) throws IOException {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (fileName.endsWith(".csv") || fileName.endsWith(".tsv") || fileName.endsWith(".txt")) {
            return readDelimitedTable(path);
        }
        return readWorkbookTable(path);
    }

    private List<List<String>> readWorkbookTable(Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path); Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            List<List<String>> rows = new ArrayList<>(lastRowNum + 1);
            for (int i = 0; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    rows.add(null);
                    continue;
                }
                int lastCellNum = row.getLastCellNum();
                List<String> values = new ArrayList<>(lastCellNum);
                for (int j = 0; j < lastCellNum; j++) {
                    values.add(readCell(row.getCell(j)));
                }
                rows.add(values);
            }
            return rows;
        }
    }

    private List<List<String>> readDelimitedTable(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            return List.of();
        }
        char delimiter = detectDelimiter(lines);
        List<List<String>> rows = new ArrayList<>(lines.size());
        for (String line : lines) {
            if (line == null) {
                rows.add(null);
                continue;
            }
            if (line.isEmpty()) {
                rows.add(List.of());
                continue;
            }
            rows.add(parseDelimitedLine(line, delimiter));
        }
        return rows;
    }

    private char detectDelimiter(List<String> lines) {
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int semicolons = countOccurrences(trimmed, ';');
            int commas = countOccurrences(trimmed, ',');
            int tabs = countOccurrences(trimmed, '\t');
            if (tabs > commas && tabs > semicolons) {
                return '\t';
            }
            if (semicolons >= commas) {
                return semicolons > 0 ? ';' : ',';
            }
            return ',';
        }
        return ',';
    }

    private int countOccurrences(String value, char symbol) {
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == symbol) {
                count++;
            }
        }
        return count;
    }

    private List<String> parseDelimitedLine(String line, char delimiter) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean firstCell = true;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == delimiter && !inQuotes) {
                values.add(normalizeDelimitedValue(current.toString(), firstCell));
                current.setLength(0);
                firstCell = false;
            } else {
                current.append(ch);
            }
        }
        values.add(normalizeDelimitedValue(current.toString(), firstCell));
        return values;
    }

    private String readCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return TextUtils.nullIfBlank(cell.getStringCellValue());
            case NUMERIC:
                double numericValue = cell.getNumericCellValue();
                long longValue = Math.round(numericValue);
                if (Math.abs(numericValue - longValue) < 0.0001) {
                    return String.valueOf(longValue);
                }
                return String.format(Locale.US, "%.2f", numericValue);
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private String normalizeDelimitedValue(String raw, boolean firstCell) {
        if (raw == null) {
            return null;
        }
        String candidate = raw;
        if (firstCell && !candidate.isEmpty() && candidate.charAt(0) == '\uFEFF') {
            candidate = candidate.substring(1);
        }
        return TextUtils.nullIfBlank(candidate);
    }

    private String readString(List<String> row, int index) {
        if (row == null || index < 0 || index >= row.size()) {
            return null;
        }
        return TextUtils.nullIfBlank(row.get(index));
    }

    private Country resolveCountry(String code) {
        if (code == null || code.isBlank()) {
            return directoryService.ensureCountry("Россия");
        }
        String normalized = code.trim();
        Country mapped = findCountryByCode(normalized);
        if (mapped != null) {
            return mapped;
        }
        return directoryService.ensureCountry(normalized);
    }

    private Country findCountryByCode(String code) {
        Country fromRaw = countriesByOriginalCode.get(code);
        if (fromRaw != null) {
            return fromRaw;
        }
        Integer numeric = parseInteger(code);
        if (numeric != null) {
            Country byNumeric = countriesByIsoNumeric.get(numeric);
            if (byNumeric != null) {
                return byNumeric;
            }
            Country byRow = countriesByRowIndex.get(numeric);
            if (byRow != null) {
                return byRow;
            }
            byRow = countriesByRowIndex.get(numeric - 1);
            if (byRow != null) {
                return byRow;
            }
        }
        if (code.length() == 2) {
            return countriesByAlpha2.get(code.toUpperCase(Locale.ROOT));
        }
        return null;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            double numeric = Double.parseDouble(value.trim());
            return (int) Math.round(numeric);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
