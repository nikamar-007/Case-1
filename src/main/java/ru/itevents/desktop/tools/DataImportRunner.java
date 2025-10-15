package ru.itevents.desktop.tools;

import ru.itevents.desktop.config.AppConfig;
import ru.itevents.desktop.service.DataImportService;

import java.nio.file.Path;

/**
 * Utility entry point that populates the MySQL database using the Excel datasets shipped with the repository.
 * Execute with: {@code mvn -q exec:java -Dexec.mainClass=ru.itevents.desktop.tools.DataImportRunner}
 */
public final class DataImportRunner {
    private DataImportRunner() {
    }

    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfig.getInstance();
        DataImportService importer = new DataImportService();
        importer.importCountries(resolve(config.getDataPath("data.countries")));
        importer.importCities(resolve(config.getDataPath("data.cities")), "Россия");
        importer.importPeople(resolve(config.getDataPath("data.organizers")), ru.itevents.desktop.model.PersonRole.ORGANIZER);
        importer.importPeople(resolve(config.getDataPath("data.moderators")), ru.itevents.desktop.model.PersonRole.MODERATOR);
        importer.importPeople(resolve(config.getDataPath("data.jury")), ru.itevents.desktop.model.PersonRole.JURY);
        importer.importPeople(resolve(config.getDataPath("data.participants")), ru.itevents.desktop.model.PersonRole.PARTICIPANT);
        importer.importEventsAndActivities(resolve(config.getDataPath("data.activities")));
        Path additional = config.getDataPath("data.additionalEvents");
        if (additional != null && !additional.toString().isBlank()) {
            importer.importEventsAndActivities(resolve(additional));
        }
        System.out.println("Данные успешно импортированы в базу");
    }

    private static Path resolve(Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        return Path.of(System.getProperty("user.dir")).resolve(path);
    }
}
