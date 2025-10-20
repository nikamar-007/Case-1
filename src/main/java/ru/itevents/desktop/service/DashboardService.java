package ru.itevents.desktop.service;

import ru.itevents.desktop.util.JdbcUtils;

public class DashboardService {
    private static final String COUNT_EVENTS = "SELECT COUNT(*) FROM events";
    private static final String COUNT_ACTIVITIES = "SELECT COUNT(*) FROM activities";
    private static final String COUNT_PARTICIPANTS = "SELECT COUNT(*) FROM people WHERE role = 'PARTICIPANT'";
    private static final String COUNT_MODERATORS = "SELECT COUNT(*) FROM people WHERE role = 'MODERATOR'";

    public long countEvents() {
        return queryForLong(COUNT_EVENTS);
    }

    public long countActivities() {
        return queryForLong(COUNT_ACTIVITIES);
    }

    public long countParticipants() {
        return queryForLong(COUNT_PARTICIPANTS);
    }

    public long countModerators() {
        return queryForLong(COUNT_MODERATORS);
    }

    private long queryForLong(String sql) {
        return JdbcUtils.queryOne(sql, null, rs -> {
            try {
                return rs.getLong(1);
            } catch (java.sql.SQLException ex) {
                throw new IllegalStateException("Failed to read numeric value", ex);
            }
        });
    }
}
