package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.Exceptions.FilmIdException;
import ru.yandex.practicum.filmorate.Exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.model.feed.EventOperation.*;
import static ru.yandex.practicum.filmorate.model.feed.EventType.*;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.EXISTING_FILM_ID;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.EXISTING_USER_ID;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceImplTest extends BaseTest {

    private final FilmService filmService;
    private final EventFeedService eventFeedService;

    @Test
    void whenAddLikeByNonExistingUser_eventNotSavedToDB() {
        assertThrows(
                UserIdException.class,
                () -> filmService.putLike(EXISTING_FILM_ID, 1000)
        );
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(1000);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenAddLikeToNonExistingFilm_eventNotSavedToDB() {
        assertThrows(
                FilmIdException.class,
                () -> filmService.putLike(1000, EXISTING_USER_ID)
        );
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenDeleteLikeOfNonExistingUser_eventNotSavedToDB() {
        assertThrows(
                UserIdException.class,
                () -> filmService.deleteLike(EXISTING_FILM_ID, 1000)
        );
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(1000);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenDeleteLikeOfNonExistingFilm_eventNotSavedToDB() {
        assertThrows(
                FilmIdException.class,
                () -> filmService.deleteLike(1000, EXISTING_USER_ID)
        );
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenAddLikeToFilm_eventSavedToDB() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID);

        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(1);
        Event event = eventFeedForUser.get(0);
        assertThat(event.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(event.getEntityId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(event.getEventType()).isEqualTo(LIKE);
        assertThat(event.getOperation()).isEqualTo(ADD);
    }

    @Test
    void whenAddLikeThenRemoveLike_twoEventsSavedToDB() {
        filmService.putLike(EXISTING_FILM_ID, EXISTING_USER_ID);
        filmService.deleteLike(EXISTING_FILM_ID, EXISTING_USER_ID);

        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(2);
        Event addLikeEvent = eventFeedForUser.stream()
                .filter(e -> e.getOperation().equals(ADD))
                .collect(Collectors.toList())
                .get(0);

        Event removeLikeEvent = eventFeedForUser.stream()
                .filter(e -> e.getOperation().equals(REMOVE))
                .collect(Collectors.toList())
                .get(0);

        assertThat(addLikeEvent.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(addLikeEvent.getEntityId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(addLikeEvent.getEventType()).isEqualTo(LIKE);
        assertThat(addLikeEvent.getOperation()).isEqualTo(ADD);

        assertThat(removeLikeEvent.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(removeLikeEvent.getEntityId()).isEqualTo(EXISTING_FILM_ID);
        assertThat(removeLikeEvent.getEventType()).isEqualTo(LIKE);
        assertThat(removeLikeEvent.getOperation()).isEqualTo(REMOVE);
    }

}