package ru.itevents.desktop.repository;

import ru.itevents.desktop.model.City;
import ru.itevents.desktop.model.Event;
import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.util.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EventRepository {
    private static final String BASE_SELECT = "SELECT e.id, e.title, e.start_date, e.duration_days, e.city_id, c.name AS city_name, e.description, e.banner_path, e.curator_id, p.full_name AS curator_name FROM events e LEFT JOIN cities c ON c.id = e.city_id LEFT JOIN people p ON p.id = e.curator_id";
    private static final String SELECT_ALL = BASE_SELECT + " ORDER BY e.start_date DESC, e.title";
    private static final String SELECT_BY_ID = BASE_SELECT + " WHERE e.id = ?";
    private static final String SELECT_BY_TITLE = BASE_SELECT + " WHERE e.title = ?";
    private static final String INSERT = "INSERT INTO events(title, start_date, duration_days, city_id, description, banner_path, curator_id) VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE events SET title=?, start_date=?, duration_days=?, city_id=?, description=?, banner_path=?, curator_id=? WHERE id=?";
    private static final String DELETE = "DELETE FROM events WHERE id=?";

    public List<Event> findAll() {
        return JdbcUtils.query(SELECT_ALL, null, this::mapRow);
    }

    public Event findById(long id) {
        return JdbcUtils.queryOne(SELECT_BY_ID, ps -> ps.setLong(1, id), this::mapRow);
    }

    public Event findByTitle(String title) {
        return JdbcUtils.queryOne(SELECT_BY_TITLE, ps -> ps.setString(1, title), this::mapRow);
    }

    public long insert(Event event) {
        return JdbcUtils.insert(INSERT, ps -> {
            ps.setString(1, event.getTitle());
            if (event.getStartDate() == null) {
                ps.setNull(2, java.sql.Types.DATE);
            } else {
                ps.setObject(2, event.getStartDate());
            }
            if (event.getDurationDays() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, event.getDurationDays());
            }
            if (event.getCity() == null || event.getCity().getId() == null) {
                ps.setNull(4, java.sql.Types.BIGINT);
            } else {
                ps.setLong(4, event.getCity().getId());
            }
            ps.setString(5, event.getDescription());
            ps.setString(6, event.getBannerPath());
            if (event.getCurator() == null || event.getCurator().getId() == null) {
                ps.setNull(7, java.sql.Types.BIGINT);
            } else {
                ps.setLong(7, event.getCurator().getId());
            }
        });
    }

    public void update(Event event) {
        JdbcUtils.update(UPDATE, ps -> {
            ps.setString(1, event.getTitle());
            if (event.getStartDate() == null) {
                ps.setNull(2, java.sql.Types.DATE);
            } else {
                ps.setObject(2, event.getStartDate());
            }
            if (event.getDurationDays() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, event.getDurationDays());
            }
            if (event.getCity() == null || event.getCity().getId() == null) {
                ps.setNull(4, java.sql.Types.BIGINT);
            } else {
                ps.setLong(4, event.getCity().getId());
            }
            ps.setString(5, event.getDescription());
            ps.setString(6, event.getBannerPath());
            if (event.getCurator() == null || event.getCurator().getId() == null) {
                ps.setNull(7, java.sql.Types.BIGINT);
            } else {
                ps.setLong(7, event.getCurator().getId());
            }
            ps.setLong(8, event.getId());
        });
    }

    public void delete(long id) {
        JdbcUtils.update(DELETE, ps -> ps.setLong(1, id));
    }

    private Event mapRow(ResultSet rs) {
        try {
            Event event = new Event();
            event.setId(rs.getLong("id"));
            event.setTitle(rs.getString("title"));
            java.sql.Date startDate = rs.getDate("start_date");
            event.setStartDate(startDate != null ? startDate.toLocalDate() : null);
            Integer duration = rs.getObject("duration_days") != null ? rs.getInt("duration_days") : null;
            event.setDurationDays(duration);
            Long cityId = rs.getObject("city_id") != null ? rs.getLong("city_id") : null;
            if (cityId != null) {
                event.setCity(new City(cityId, rs.getString("city_name")));
            }
            event.setDescription(rs.getString("description"));
            event.setBannerPath(rs.getString("banner_path"));
            Long curatorId = rs.getObject("curator_id") != null ? rs.getLong("curator_id") : null;
            if (curatorId != null) {
                event.setCurator(new Person(curatorId, PersonRole.ORGANIZER, rs.getString("curator_name")));
            }
            return event;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to map event row", ex);
        }
    }
}
