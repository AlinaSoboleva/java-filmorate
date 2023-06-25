package ru.yandex.practicum.filmorate.storage.feed.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.storage.feed.EventFeedDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventFeedDaoImpl implements EventFeedDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveEvent(Event event) {
        String sql = "INSERT INTO Events(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                     "VALUES (?, ?, ?, ?, ?)";
        log.debug("Executing SQL statement {} to save Event {}", sql, event);
        jdbcTemplate.update(sql,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId());
    }

    @Override
    public List<Event> getEventFeedForUser(int userId) {
        String sql = "SELECT * FROM Events WHERE user_id = ?";
        List<Event> events = jdbcTemplate.query(sql, this::makeEvent, userId);
        log.debug("Retrieved events of user with id={}. List of events={}", userId, events);
        return events;
    }

    private Event makeEvent(ResultSet rs, int row) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getInt("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("operation")))
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}
