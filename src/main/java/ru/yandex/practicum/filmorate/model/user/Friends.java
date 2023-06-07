package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;

@Data
public class Friends {
    private final int userId;
    private final int friendId;
    private boolean friendsStatus = false;

}
