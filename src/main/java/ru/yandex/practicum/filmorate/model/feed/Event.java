package ru.yandex.practicum.filmorate.model.feed;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private long eventId;
    private long timestamp;
    private int userId;
    private EventType eventType;
    private EventOperation operation;
    private int entityId;
}
