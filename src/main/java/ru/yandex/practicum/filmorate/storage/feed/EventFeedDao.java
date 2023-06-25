package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;

public interface EventFeedDao {
    void saveEvent(Event event);

    List<Event> getEventFeedForUser(int userId);
}
