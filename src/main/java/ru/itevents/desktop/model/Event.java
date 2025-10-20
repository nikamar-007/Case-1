package ru.itevents.desktop.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private Long id;
    private String title;
    private LocalDate startDate;
    private Integer durationDays;
    private City city;
    private String description;
    private String bannerPath;
    private Person curator;
    private final List<Activity> activities = new ArrayList<>();

    public Event() {
    }

    public Event(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerPath() {
        return bannerPath;
    }

    public void setBannerPath(String bannerPath) {
        this.bannerPath = bannerPath;
    }

    public Person getCurator() {
        return curator;
    }

    public void setCurator(Person curator) {
        this.curator = curator;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    @Override
    public String toString() {
        return title;
    }
}
