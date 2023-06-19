package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTest;
import ru.yandex.practicum.filmorate.Exceptions.UserIdException;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.FriendsService;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.model.feed.EventOperation.ADD;
import static ru.yandex.practicum.filmorate.model.feed.EventOperation.REMOVE;
import static ru.yandex.practicum.filmorate.model.feed.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.EXISTING_FRIEND_ID;
import static ru.yandex.practicum.filmorate.testdata.TestConstants.EXISTING_USER_ID;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendsServiceImplTest extends BaseTest {

    private final FriendsService friendsService;
    private final EventFeedService eventFeedService;

    @Test
    void whenDeleteFriendOfNonExistingUser_eventNotSaved() {
        friendsService.deleteFriend(1000, 1000);
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(1000);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenAddFriendOfNonExistingUser_eventNotSaved() {
        assertThrows(
                UserIdException.class,
                () -> friendsService.addFriend(1000, EXISTING_FRIEND_ID)
        );
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(1000);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenAddNonExistingFriendOfExistingUser_eventNotSaved() {
        assertThrows(
                UserIdException.class,
                () -> friendsService.addFriend(EXISTING_USER_ID, 1000)
        );
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser).isEmpty();
    }

    @Test
    void whenAddFriend_eventSavedToDB() {
        friendsService.addFriend(EXISTING_USER_ID, EXISTING_FRIEND_ID);
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(1);
        Event event = eventFeedForUser.get(0);
        assertThat(event.getEventType()).isEqualTo(FRIEND);
        assertThat(event.getOperation()).isEqualTo(ADD);
        assertThat(event.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(event.getEntityId()).isEqualTo(EXISTING_FRIEND_ID);
    }

    @Test
    void whenAddThenRemoveFriend_twoEventsSavedToDB() {
        friendsService.addFriend(EXISTING_USER_ID, EXISTING_FRIEND_ID);
        friendsService.deleteFriend(EXISTING_USER_ID, EXISTING_FRIEND_ID);
        List<Event> eventFeedForUser = eventFeedService.getEventFeedForUser(EXISTING_USER_ID);
        assertThat(eventFeedForUser.size()).isEqualTo(2);
        Event addEvent = eventFeedForUser.stream()
                .filter(e -> e.getOperation().equals(ADD))
                .collect(Collectors.toList())
                .get(0);

        Event deleteEvent = eventFeedForUser.stream()
                .filter(e -> e.getOperation().equals(REMOVE))
                .collect(Collectors.toList())
                .get(0);

        assertThat(addEvent.getEventType()).isEqualTo(FRIEND);
        assertThat(addEvent.getOperation()).isEqualTo(ADD);
        assertThat(addEvent.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(addEvent.getEntityId()).isEqualTo(EXISTING_FRIEND_ID);

        assertThat(deleteEvent.getEventType()).isEqualTo(FRIEND);
        assertThat(deleteEvent.getOperation()).isEqualTo(REMOVE);
        assertThat(deleteEvent.getUserId()).isEqualTo(EXISTING_USER_ID);
        assertThat(deleteEvent.getEntityId()).isEqualTo(EXISTING_FRIEND_ID);
    }

}