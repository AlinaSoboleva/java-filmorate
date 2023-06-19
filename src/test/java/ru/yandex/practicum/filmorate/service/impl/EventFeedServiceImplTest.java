package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.service.EventFeedService;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.model.feed.EventOperation.ADD;
import static ru.yandex.practicum.filmorate.model.feed.EventOperation.REMOVE;
import static ru.yandex.practicum.filmorate.model.feed.EventType.*;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.EXISTING_USER_ID;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventFeedServiceImplTest extends BaseTest {

    private final EventFeedService sut;

    @Test
    void whenSaveEventsOfDifferentType_getEventsReturnsAllEvents() {
        sut.saveEvent(FRIEND, ADD, EXISTING_USER_ID, 2);
        sut.saveEvent(FRIEND, REMOVE, EXISTING_USER_ID, 2);
        sut.saveEvent(LIKE, ADD, EXISTING_USER_ID, 1);
        sut.saveEvent(LIKE, REMOVE, EXISTING_USER_ID, 1);
        sut.saveEvent(REVIEW, ADD, EXISTING_USER_ID, 1);
        sut.saveEvent(REVIEW, REMOVE, EXISTING_USER_ID, 1);

        List<Event> eventFeedForUser = sut.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(6);
    }

    @Test
    void whenNoEventsSaved_getEventsReturnsEmptyList() {
        List<Event> eventFeedForUser = sut.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenSaveEventOfExistingUser_eventAddedToDb() {
        sut.saveEvent(LIKE, ADD, EXISTING_USER_ID, 1);
        List<Event> eventFeedForUser = sut.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(1);
        Event event = eventFeedForUser.get(0);
        assertThat(event.getEventType()).isEqualTo(LIKE);
        assertThat(event.getOperation()).isEqualTo(ADD);
        assertThat(event.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(event.getEntityId()).isEqualTo(1);
    }

    @Test
    void whenSaveEventOfNonExistingUser_throwsDataIntegrityViolation() {
        assertThrows(
                DataIntegrityViolationException.class,
                () -> sut.saveEvent(LIKE, ADD, 1000, 1)
        );
    }

}