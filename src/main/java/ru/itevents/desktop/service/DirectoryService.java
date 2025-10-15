package ru.itevents.desktop.service;

import ru.itevents.desktop.model.City;
import ru.itevents.desktop.model.Country;
import ru.itevents.desktop.repository.CityRepository;
import ru.itevents.desktop.repository.CountryRepository;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DirectoryService {
    private final CountryRepository countryRepository = new CountryRepository();
    private final CityRepository cityRepository = new CityRepository();

    public List<Country> getCountries() {
        return countryRepository.findAll();
    }

    public List<City> getCities() {
        return cityRepository.findAll();
    }

    public Country ensureCountry(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Country name must not be blank");
        }
        Country country = new Country();
        country.setName(name.trim());
        return saveCountry(country);
    }

    public Country saveCountry(Country country) {
        Objects.requireNonNull(country, "country");
        Country existing = null;
        if (country.getIsoNumeric() != null) {
            existing = countryRepository.findByIsoNumeric(country.getIsoNumeric());
        }
        if (existing == null && country.getIsoAlpha2() != null && !country.getIsoAlpha2().isBlank()) {
            existing = countryRepository.findByIsoAlpha2(country.getIsoAlpha2().toUpperCase(Locale.ROOT));
        }
        if (existing == null && country.getName() != null && !country.getName().isBlank()) {
            existing = countryRepository.findByName(country.getName());
        }
        if (existing != null) {
            existing.setName(country.getName());
            existing.setEnglishName(country.getEnglishName());
            existing.setIsoAlpha2(country.getIsoAlpha2());
            existing.setIsoNumeric(country.getIsoNumeric());
            countryRepository.update(existing);
            return existing;
        }
        long id = countryRepository.insert(country);
        country.setId(id);
        return country;
    }

    public Country findCountryByIsoNumeric(Integer isoNumeric) {
        return countryRepository.findByIsoNumeric(isoNumeric);
    }

    public Country findCountryByIsoAlpha2(String isoAlpha2) {
        if (isoAlpha2 == null || isoAlpha2.isBlank()) {
            return null;
        }
        return countryRepository.findByIsoAlpha2(isoAlpha2.toUpperCase(Locale.ROOT));
    }

    public City ensureCity(String name, Country country) {
        City existing = cityRepository.findByName(name);
        if (existing != null) {
            return existing;
        }
        City city = new City();
        city.setName(name);
        city.setCountry(country);
        long id = cityRepository.insert(city);
        city.setId(id);
        return city;
    }
}
