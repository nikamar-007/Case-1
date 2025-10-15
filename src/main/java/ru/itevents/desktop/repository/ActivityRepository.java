package ru.itevents.desktop.repository;

import ru.itevents.desktop.model.Activity;
import ru.itevents.desktop.model.Event;
import ru.itevents.desktop.model.Person;
import ru.itevents.desktop.model.PersonRole;
import ru.itevents.desktop.util.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class ActivityRepository {
    private static final String BASE_SELECT = "SELECT a.id, a.event_id, a.title, a.day_number, a.start_time, a.moderator_id, m.full_name AS moderator_name, a.winner_id, w.full_name AS winner_name FROM activities a LEFT JOIN people m ON m.id = a.moderator_id LEFT JOIN people w ON w.id = a.winner_id";
    private static final String SELECT_BY_EVENT = BASE_SELECT + " WHERE a.event_id = ? ORDER BY a.day_number, a.start_time";
    private static final String INSERT = "INSERT INTO activities(event_id, title, day_number, start_time, moderator_id, winner_id, description) VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE activities SET title=?, day_number=?, start_time=?, moderator_id=?, winner_id=?, description=? WHERE id=?";
    private static final String DELETE = "DELETE FROM activities WHERE id=?";

    private static final String DELETE_JURY = "DELETE FROM activity_jury WHERE activity_id=?";
    private static final String INSERT_JURY = "INSERT INTO activity_jury(activity_id, jury_id, jury_order) VALUES(?, ?, ?)";
    private static final String DELETE_PARTICIPANTS = "DELETE FROM activity_participants WHERE activity_id=?";
    private static final String INSERT_PARTICIPANT = "INSERT INTO activity_participants(activity_id, participant_id) VALUES(?, ?)";
    private static final String SELECT_JURY = "SELECT aj.jury_id, p.full_name FROM activity_jury aj JOIN people p ON p.id = aj.jury_id WHERE aj.activity_id = ? ORDER BY aj.jury_order";

    private final PersonRepository personRepository = new PersonRepository();

    public List<Activity> findByEvent(long eventId) {
        List<Activity> activities = JdbcUtils.query(SELECT_BY_EVENT, ps -> ps.setLong(1, eventId), this::mapRow);
        for (Activity activity : activities) {
            activity.getJury().clear();
            activity.getJury().addAll(loadJury(activity.getId()));
        }
        return activities;
    }

    public long insert(Activity activity) {
        long id = JdbcUtils.insert(INSERT, ps -> {
            ps.setLong(1, activity.getEvent().getId());
            ps.setString(2, activity.getTitle());
            if (activity.getDayNumber() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, activity.getDayNumber());
            }
            if (activity.getStartTime() == null) {
                ps.setNull(4, java.sql.Types.TIME);
            } else {
                ps.setObject(4, activity.getStartTime());
            }
            if (activity.getModerator() == null || activity.getModerator().getId() == null) {
                ps.setNull(5, java.sql.Types.BIGINT);
            } else {
                ps.setLong(5, activity.getModerator().getId());
            }
            if (activity.getWinner() == null || activity.getWinner().getId() == null) {
                ps.setNull(6, java.sql.Types.BIGINT);
            } else {
                ps.setLong(6, activity.getWinner().getId());
            }
            ps.setString(7, null);
        });
        activity.setId(id);
        syncRelations(activity);
        return id;
    }

    public void update(Activity activity) {
        JdbcUtils.update(UPDATE, ps -> {
            ps.setString(1, activity.getTitle());
            if (activity.getDayNumber() == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, activity.getDayNumber());
            }
            if (activity.getStartTime() == null) {
                ps.setNull(3, java.sql.Types.TIME);
            } else {
                ps.setObject(3, activity.getStartTime());
            }
            if (activity.getModerator() == null || activity.getModerator().getId() == null) {
                ps.setNull(4, java.sql.Types.BIGINT);
            } else {
                ps.setLong(4, activity.getModerator().getId());
            }
            if (activity.getWinner() == null || activity.getWinner().getId() == null) {
                ps.setNull(5, java.sql.Types.BIGINT);
            } else {
                ps.setLong(5, activity.getWinner().getId());
            }
            ps.setString(6, null);
            ps.setLong(7, activity.getId());
        });
        syncRelations(activity);
    }

    public void delete(long id) {
        JdbcUtils.update(DELETE_PARTICIPANTS, ps -> ps.setLong(1, id));
        JdbcUtils.update(DELETE_JURY, ps -> ps.setLong(1, id));
        JdbcUtils.update(DELETE, ps -> ps.setLong(1, id));
    }

    private void syncRelations(Activity activity) {
        JdbcUtils.update(DELETE_JURY, ps -> ps.setLong(1, activity.getId()));
        int order = 1;
        for (Person jury : activity.getJury()) {
            long juryId = ensurePersonExists(jury, PersonRole.JURY);
            final int finalOrder = order++;
            JdbcUtils.insert(INSERT_JURY, ps -> {
                ps.setLong(1, activity.getId());
                ps.setLong(2, juryId);
                ps.setInt(3, finalOrder);
            });
        }
        JdbcUtils.update(DELETE_PARTICIPANTS, ps -> ps.setLong(1, activity.getId()));
    }

    private long ensurePersonExists(Person person, PersonRole role) {
        if (person == null) {
            throw new IllegalArgumentException("Person must not be null");
        }
        if (person.getId() != null) {
            return person.getId();
        }
        person.setRole(role);
        Person existing = personRepository.findByFullName(person.getFullName());
        if (existing != null) {
            person.setId(existing.getId());
            return existing.getId();
        }
        return personRepository.insert(person);
    }

    private java.util.List<Person> loadJury(long activityId) {
        return JdbcUtils.query(SELECT_JURY, ps -> ps.setLong(1, activityId), rs -> {
            try {
                long id = rs.getLong("jury_id");
                String name = rs.getString("full_name");
                return new Person(id, PersonRole.JURY, name);
            } catch (SQLException ex) {
                throw new IllegalStateException("Failed to map jury", ex);
            }
        });
    }

    private Activity mapRow(ResultSet rs) {
        try {
            Activity activity = new Activity();
            activity.setId(rs.getLong("id"));
            Event event = new Event();
            event.setId(rs.getLong("event_id"));
            activity.setEvent(event);
            activity.setTitle(rs.getString("title"));
            activity.setDayNumber(rs.getObject("day_number") != null ? rs.getInt("day_number") : null);
            java.sql.Time time = rs.getTime("start_time");
            activity.setStartTime(time != null ? time.toLocalTime() : null);
            Long moderatorId = rs.getObject("moderator_id") != null ? rs.getLong("moderator_id") : null;
            if (moderatorId != null) {
                activity.setModerator(new Person(moderatorId, PersonRole.MODERATOR, rs.getString("moderator_name")));
            }
            Long winnerId = rs.getObject("winner_id") != null ? rs.getLong("winner_id") : null;
            if (winnerId != null) {
                activity.setWinner(new Person(winnerId, PersonRole.PARTICIPANT, rs.getString("winner_name")));
            }
            return activity;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to map activity row", ex);
        }
    }
}
