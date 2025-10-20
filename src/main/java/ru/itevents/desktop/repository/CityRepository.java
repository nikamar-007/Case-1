package ru.itevents.desktop.repository;

import ru.itevents.desktop.model.City;
import ru.itevents.desktop.model.Country;
import ru.itevents.desktop.util.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CityRepository {
    private static final String SELECT_ALL = "SELECT c.id, c.name, c.country_id, co.name AS country_name FROM cities c LEFT JOIN countries co ON co.id = c.country_id ORDER BY c.name";
    private static final String FIND_BY_NAME = "SELECT c.id, c.name, c.country_id, co.name AS country_name FROM cities c LEFT JOIN countries co ON co.id = c.country_id WHERE c.name = ?";
    private static final String INSERT = "INSERT INTO cities(name, country_id) VALUES(?, ?)";

    public List<City> findAll() {
        return JdbcUtils.query(SELECT_ALL, null, this::mapRow);
    }

    public City findByName(String name) {
        return JdbcUtils.queryOne(FIND_BY_NAME, ps -> ps.setString(1, name), this::mapRow);
    }

    public long insert(City city) {
        return JdbcUtils.insert(INSERT, ps -> {
            ps.setString(1, city.getName());
            if (city.getCountry() == null || city.getCountry().getId() == null) {
                ps.setNull(2, java.sql.Types.BIGINT);
            } else {
                ps.setLong(2, city.getCountry().getId());
            }
        });
    }

    private City mapRow(ResultSet rs) {
        try {
            Country country = null;
            Long countryId = rs.getObject("country_id") != null ? rs.getLong("country_id") : null;
            if (countryId != null) {
                country = new Country(countryId, rs.getString("country_name"));
            }
            return new City(rs.getLong("id"), rs.getString("name"), country);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to map city row", ex);
        }
    }
}
