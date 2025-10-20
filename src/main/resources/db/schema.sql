CREATE DATABASE IF NOT EXISTS event_master CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE event_master;

CREATE TABLE IF NOT EXISTS countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    english_name VARCHAR(120),
    iso_alpha2 CHAR(2),
    iso_numeric INT,
    UNIQUE KEY uk_countries_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    country_id BIGINT,
    UNIQUE KEY uk_cities_name (name),
    CONSTRAINT fk_cities_country FOREIGN KEY (country_id) REFERENCES countries(id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS people (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(32) NOT NULL,
    full_name VARCHAR(180) NOT NULL,
    gender VARCHAR(32),
    email VARCHAR(160),
    birth_date DATE,
    country_id BIGINT,
    phone VARCHAR(40),
    specialization VARCHAR(120),
    focus VARCHAR(160),
    password_hash VARCHAR(128),
    photo_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_people_email_role (email, role),
    CONSTRAINT fk_people_country FOREIGN KEY (country_id) REFERENCES countries(id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    start_date DATE,
    duration_days INT,
    city_id BIGINT,
    description TEXT,
    banner_path VARCHAR(255),
    curator_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_events_title (title),
    CONSTRAINT fk_events_city FOREIGN KEY (city_id) REFERENCES cities(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_events_curator FOREIGN KEY (curator_id) REFERENCES people(id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS event_organizers (
    event_id BIGINT NOT NULL,
    organizer_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, organizer_id),
    CONSTRAINT fk_event_org_event FOREIGN KEY (event_id) REFERENCES events(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_event_org_person FOREIGN KEY (organizer_id) REFERENCES people(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    day_number INT,
    start_time TIME,
    moderator_id BIGINT,
    winner_id BIGINT,
    description TEXT,
    CONSTRAINT fk_activities_event FOREIGN KEY (event_id) REFERENCES events(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_activities_moderator FOREIGN KEY (moderator_id) REFERENCES people(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_activities_winner FOREIGN KEY (winner_id) REFERENCES people(id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS activity_jury (
    activity_id BIGINT NOT NULL,
    jury_id BIGINT NOT NULL,
    jury_order INT,
    PRIMARY KEY (activity_id, jury_id),
    CONSTRAINT fk_activity_jury_activity FOREIGN KEY (activity_id) REFERENCES activities(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_activity_jury_person FOREIGN KEY (jury_id) REFERENCES people(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS activity_participants (
    activity_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    PRIMARY KEY (activity_id, participant_id),
    CONSTRAINT fk_activity_part_activity FOREIGN KEY (activity_id) REFERENCES activities(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_activity_part_person FOREIGN KEY (participant_id) REFERENCES people(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE VIEW IF NOT EXISTS v_event_summary AS
SELECT e.id,
       e.title,
       e.start_date,
       e.duration_days,
       COUNT(DISTINCT a.id) AS activities_count,
       COUNT(DISTINCT ap.participant_id) AS participants_count
FROM events e
LEFT JOIN activities a ON a.event_id = e.id
LEFT JOIN activity_participants ap ON ap.activity_id = a.id
GROUP BY e.id, e.title, e.start_date, e.duration_days;
