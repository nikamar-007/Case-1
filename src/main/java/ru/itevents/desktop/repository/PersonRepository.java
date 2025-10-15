package ru.itevents.desktop.repository;

import ru.itevents.desktop.model.Country;
import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.util.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PersonRepository {
    private static final String BASE_SELECT = "SELECT p.id, p.role, p.full_name, p.gender, p.email, p.birth_date, p.country_id, c.name AS country_name, p.phone, p.specialization, p.focus, p.password_hash, p.photo_path FROM people p LEFT JOIN countries c ON c.id = p.country_id";
    private static final String SELECT_BY_ROLE = BASE_SELECT + " WHERE p.role = ? ORDER BY p.full_name";
    private static final String SELECT_BY_ID = BASE_SELECT + " WHERE p.id = ?";
    private static final String SELECT_BY_NAME = BASE_SELECT + " WHERE p.full_name = ?";
    private static final String SEARCH_BY_NAME = BASE_SELECT + " WHERE p.role = ? AND LOWER(p.full_name) LIKE ? ORDER BY p.full_name";
    private static final String INSERT = "INSERT INTO people(role, full_name, gender, email, birth_date, country_id, phone, specialization, focus, password_hash, photo_path) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE people SET full_name=?, gender=?, email=?, birth_date=?, country_id=?, phone=?, specialization=?, focus=?, password_hash=?, photo_path=? WHERE id=?";
    private static final String DELETE = "DELETE FROM people WHERE id=?";

    public List<Person> findByRole(PersonRole role) {
        return JdbcUtils.query(SELECT_BY_ROLE, ps -> ps.setString(1, role.name()), this::mapRow);
    }

    public Person findById(long id) {
        return JdbcUtils.queryOne(SELECT_BY_ID, ps -> ps.setLong(1, id), this::mapRow);
    }

    public Person findByFullName(String name) {
        return JdbcUtils.queryOne(SELECT_BY_NAME, ps -> ps.setString(1, name), this::mapRow);
    }

    public List<Person> search(PersonRole role, String query) {
        String like = "%" + query.toLowerCase() + "%";
        return JdbcUtils.query(SEARCH_BY_NAME, ps -> {
            ps.setString(1, role.name());
            ps.setString(2, like);
        }, this::mapRow);
    }

    public long insert(Person person) {
        return JdbcUtils.insert(INSERT, ps -> setPersonFields(ps, person));
    }

    public void update(Person person) {
        JdbcUtils.update(UPDATE, ps -> {
            setPersonFields(ps, person);
            ps.setLong(11, person.getId());
        });
    }

    public void delete(long id) {
        JdbcUtils.update(DELETE, ps -> ps.setLong(1, id));
    }

    private void setPersonFields(java.sql.PreparedStatement ps, Person person) throws SQLException {
        ps.setString(1, person.getRole().name());
        ps.setString(2, person.getFullName());
        ps.setString(3, person.getGender());
        ps.setString(4, person.getEmail());
        if (person.getBirthDate() == null) {
            ps.setNull(5, java.sql.Types.DATE);
        } else {
            ps.setObject(5, person.getBirthDate());
        }
        if (person.getCountry() == null || person.getCountry().getId() == null) {
            ps.setNull(6, java.sql.Types.BIGINT);
        } else {
            ps.setLong(6, person.getCountry().getId());
        }
        ps.setString(7, person.getPhone());
        ps.setString(8, person.getSpecialization());
        ps.setString(9, person.getFocus());
        ps.setString(10, person.getPasswordHash());
        ps.setString(11, person.getPhotoPath());
    }

    private Person mapRow(ResultSet rs) {
        try {
            Person person = new Person();
            person.setId(rs.getLong("id"));
            person.setRole(PersonRole.valueOf(rs.getString("role")));
            person.setFullName(rs.getString("full_name"));
            person.setGender(rs.getString("gender"));
            person.setEmail(rs.getString("email"));
            java.sql.Date birthDate = rs.getDate("birth_date");
            person.setBirthDate(birthDate != null ? birthDate.toLocalDate() : null);
            Long countryId = rs.getObject("country_id") != null ? rs.getLong("country_id") : null;
            if (countryId != null) {
                person.setCountry(new Country(countryId, rs.getString("country_name")));
            }
            person.setPhone(rs.getString("phone"));
            person.setSpecialization(rs.getString("specialization"));
            person.setFocus(rs.getString("focus"));
            person.setPasswordHash(rs.getString("password_hash"));
            person.setPhotoPath(rs.getString("photo_path"));
            return person;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to map person row", ex);
        }
    }
}
