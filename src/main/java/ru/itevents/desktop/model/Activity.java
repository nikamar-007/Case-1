package ru.itevents.desktop.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Activity {
    private Long id;
    private Event event;
    private String title;
    private Integer dayNumber;
    private LocalTime startTime;
    private Person moderator;
    private Person winner;
    private final List<Person> jury = new ArrayList<>();

    public Activity() {
    }

    public Activity(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Person getModerator() {
        return moderator;
    }

    public void setModerator(Person moderator) {
        this.moderator = moderator;
    }

    public Person getWinner() {
        return winner;
    }

    public void setWinner(Person winner) {
        this.winner = winner;
    }

    public List<Person> getJury() {
        return jury;
    }

    @Override
    public String toString() {
        return title;
    }
}
