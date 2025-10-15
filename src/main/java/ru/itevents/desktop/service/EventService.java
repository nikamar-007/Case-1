package ru.itevents.desktop.service;

import ru.itevents.desktop.model.Activity;
import ru.itevents.desktop.model.Event;
import ru.itevents.desktop.repository.ActivityRepository;
import ru.itevents.desktop.repository.EventRepository;

import java.util.List;

public class EventService {
    private final EventRepository eventRepository = new EventRepository();
    private final ActivityRepository activityRepository = new ActivityRepository();

    public List<Event> findAll() {
        List<Event> events = eventRepository.findAll();
        events.forEach(event -> event.getActivities().addAll(activityRepository.findByEvent(event.getId())));
        return events;
    }

    public Event save(Event event) {
        if (event.getId() == null) {
            long id = eventRepository.insert(event);
            event.setId(id);
        } else {
            eventRepository.update(event);
        }
        return event;
    }

    public void delete(long eventId) {
        eventRepository.delete(eventId);
    }

    public Activity saveActivity(Activity activity) {
        if (activity.getId() == null) {
            long id = activityRepository.insert(activity);
            activity.setId(id);
        } else {
            activityRepository.update(activity);
        }
        return activity;
    }

    public void deleteActivity(long activityId) {
        activityRepository.delete(activityId);
    }
}
