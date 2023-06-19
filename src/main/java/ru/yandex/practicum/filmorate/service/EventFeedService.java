package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;

import java.util.List;

public interface EventFeedService {

    void saveEvent(EventType eventType,
                   EventOperation operation,
                   int userId,
                   int entityId);

    List<Event> getEventFeedForUser(int userId);

}
