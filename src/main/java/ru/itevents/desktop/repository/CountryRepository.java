package ru.itevents.desktop.repository;

import ru.itevents.desktop.model.Country;
import ru.itevents.desktop.util.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CountryRepository {
    private static final String SELECT_ALL = "SELECT id, name, english_name, iso_alpha2, iso_numeric FROM countries ORDER BY name";
    private static final String FIND_BY_NAME = "SELECT id, name, english_name, iso_alpha2, iso_numeric FROM countries WHERE name = ?";
    private static final String FIND_BY_ALPHA2 = "SELECT id, name, english_name, iso_alpha2, iso_numeric FROM countries WHERE UPPER(iso_alpha2) = UPPER(?)";
    private static final String FIND_BY_NUMERIC = "SELECT id, name, english_name, iso_alpha2, iso_numeric FROM countries WHERE iso_numeric = ?";
    private static final String INSERT = "INSERT INTO countries(name, english_name, iso_alpha2, iso_numeric) VALUES(?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE countries SET name = ?, english_name = ?, iso_alpha2 = ?, iso_numeric = ? WHERE id = ?";

    public List<Country> findAll() {
        return JdbcUtils.query(SELECT_ALL, null, this::mapRow);
    }

    public Country findByName(String name) {
        return JdbcUtils.queryOne(FIND_BY_NAME, ps -> ps.setString(1, name), this::mapRow);
    }

    public Country findByIsoAlpha2(String isoAlpha2) {
        return JdbcUtils.queryOne(FIND_BY_ALPHA2, ps -> ps.setString(1, isoAlpha2), this::mapRow);
    }

    public Country findByIsoNumeric(Integer isoNumeric) {
        if (isoNumeric == null) {
            return null;
        }
        return JdbcUtils.queryOne(FIND_BY_NUMERIC, ps -> ps.setInt(1, isoNumeric), this::mapRow);
    }

    public long insert(Country country) {
        return JdbcUtils.insert(INSERT, ps -> {
            ps.setString(1, country.getName());
            ps.setString(2, country.getEnglishName());
            ps.setString(3, country.getIsoAlpha2());
            if (country.getIsoNumeric() == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, country.getIsoNumeric());
            }
        });
    }

    public void update(Country country) {
        JdbcUtils.update(UPDATE, ps -> {
            ps.setString(1, country.getName());
            ps.setString(2, country.getEnglishName());
            ps.setString(3, country.getIsoAlpha2());
            if (country.getIsoNumeric() == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, country.getIsoNumeric());
            }
            ps.setLong(5, country.getId());
        });
    }

    private Country mapRow(ResultSet rs) {
        try {
            return new Country(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("english_name"),
                    rs.getString("iso_alpha2"),
                    rs.getObject("iso_numeric") != null ? rs.getInt("iso_numeric") : null
            );
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to map country row", ex);
        }
    }
}
