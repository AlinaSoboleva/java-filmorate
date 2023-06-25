package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.storage.feed.EventFeedDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventFeedServiceImpl implements EventFeedService {

    private final EventFeedDao eventFeedDao;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Override
    public void saveEvent(EventType eventType,
                          EventOperation operation,
                          int userId,
                          int entityId) {
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .eventType(eventType)
                .operation(operation)
                .userId(userId)
                .entityId(entityId)
                .build();

        saveEvent(event);
    }

    private void saveEvent(Event event) {
        eventFeedDao.saveEvent(event);
    }

    @Override
    public List<Event> getEventFeedForUser(int userId) {
        userStorage.validationId(userId);
        return eventFeedDao.getEventFeedForUser(userId);
    }
}
